package org.grails.plugin.easygrid

import grails.validation.ValidationErrors
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.codehaus.groovy.control.ConfigurationException
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.binding.DataBindingUtils

import static org.grails.plugin.easygrid.EasygridContextHolder.getParams
import static org.grails.plugin.easygrid.EasygridContextHolder.getSession

/**
 * utility methods
 *
 * @author <a href='mailto:tudor.malene@gmail.com'>Tudor Malene</a>
 */
@Slf4j
class GridUtils {

    /**
     * the logic of determining the sort property
     * @param gridConfig
     * @param column
     * @return
     */
    static valueOfSortColumn(gridConfig, ColumnConfig column) {
        assert column.sortable
        if (column.sortClosure) {
            return column.sortClosure
        }
        if (column.sortProperty) {
            return column.sortProperty
        }
        return column.name
    }

    /**
     * return the export value
     * @param gridConfig
     * @param column
     * @param element
     * @param idx
     */
    static valueOfExportColumn(gridConfig, ColumnConfig column, element, idx) {
        // if there is a value closure defined in the export section, evaluate that , otherwise the normal
        if (column.export?.value) {
            valueOfClosureColumn(gridConfig, column, column.export.value, element, idx)
        } else {
            valueOfColumn(gridConfig, column, element, idx)
        }
    }

    /**
     * return the value for a column from a row
     * the main link between the datasource & the grid Implementation
     * @param column - the column from the config
     * @param element - the data
     * @param row - the index - used for numberings
     * @return
     */
    static valueOfColumn(gridConfig, ColumnConfig column, element, idx) {

        if (column.property) {
            valueOfPropertyColumn(gridConfig, column, element, idx)
        } else {
            assert column.value
            valueOfClosureColumn(gridConfig, column, column.value, element, idx)
        }
    }


    static valueOfPropertyColumn(gridConfig, ColumnConfig column, element, idx) {
        assert column.property
        def val = getNestedPropertyValue(column.property, element)

        if (val == null) {
            return null
        }

        //apply the format
        if (column.formatter) {
            return column.formatter(val)
        }

        // apply the default value formats
        def formatClosure = gridConfig.formats.find { clazz, closure -> clazz.isAssignableFrom(val.getClass()) }?.value
        formatClosure ? formatClosure.call(val) : val
    }

    /**
     * returns the value from the "value" closure
     * @param column
     * @param element
     * @param idx
     * @return
     */
    static valueOfClosureColumn(gridConfig, ColumnConfig column, Closure closure, element, idx) {
        switch (closure?.parameterTypes?.size()) {
            case null:
                return ''
            case 1:
                return closure.call(element)
            case 2:
                return closure.call(element, params)
            case 3:
                return closure.call(element, params, idx + 1)
        }
    }

    /**
     * copy the values from the first map to the second only if they are not defined first
     * works on n levels
     * @param from
     * @param to
     * @param level - on how many level to copy ( -1 = infinite)
     */
    static void copyProperties(from, to, level = -1) {
        from.each { prop, val ->
            if ((val instanceof Map)) {
                if (level <= -1 || level > 1) {
                    if (to[prop] == null) {
                        to[prop] = [:]
                    }
                    if (!(to[prop] instanceof Map)) {
                        throw new ConfigurationException("Trying to copy properties from ${val} to ${to[prop]}. ${to[prop]} should be a map")
                    }
                    copyProperties(val, to[prop], level - 1)
                }
            } else {
                if (to[prop] == null) {
                    to[prop] = val
                }
            }
        }
    }

    /**
     * searches for actual grid implementations in the config section
     * @param config
     * @return
     */
    static findImplementations(Map config) {
        config.gridImplementations.collect { it.key }
    }

    /**
     * stores the last search parameters
     * in the session , to be retrieved on the next return on the page
     *
     * @param session
     * @param params
     * @param id
     */
    static void storeLastSearch(gridConfig) {

/*
        if (EasygridContextHolder.session.getAttribute(lastSearchAttr(gridConfig))) {
            def localParams = EasygridContextHolder.session.getAttribute(searchParamsAttrName) ?: EasygridContextHolder.params
            EasygridContextHolder.storeParams(localParams)
            EasygridContextHolder.session.removeAttribute(lastSearchAttr(gridConfig))
        } else {
            //save the current search param
            EasygridContextHolder.session.setAttribute(searchParamsAttrName, EasygridContextHolder.params)
        }
*/

        session.setAttribute("searchParams_${gridConfig.id}".toString(), params)
    }

    static def retreiveLastSearch(gridConfig) {
        session.getAttribute("searchParams_${gridConfig.id}".toString())
    }

    /**
     * when exporting a table or returning from an add/update screen and you want to save the old search use this
     */
/*
    static void markRestorePreviousSearch(GridConfig gridConfig) {
        session.setAttribute(lastSearchAttr(gridConfig), true)
    }

    private static String lastSearchAttr(gridConfig) {
        "restoreLastSearch${gridConfig.id}".toString()
    }
*/

    /**
     * hack to navigate nested objects
     *
     * @param expression
     * @param object
     * @return
     */
    static getNestedPropertyValue(String expression, object) {
        try {
            def val = object
            for (String fieldPart in expression.split("\\.")) {
                val = val?."$fieldPart"
            }
            val
        } catch (any) {
//            otherwise fallback to the slow implementation of Eval
            log.warn("Could not evaluate ${expression} . Trying 'eval'")
            try {
                Eval.x(object, "x.${expression}")
            } catch (Exception e) {
                log.error("could not access property ${expression} of ${object}")
                throw new RuntimeException("could not access property ${expression} of ${object}", e)
            }
        }
    }

    /**
     *
     * @param expression
     * @param object
     * @param value
     * @return
     */
    static setNestedPropertyValue(String expression, object, value) {
        Eval.xy(object, value, "x.${expression}=y")
    }

    /**
     * iterates columns - excluding certain columns depending on the context
     * @param grid
     * @param export - if we need the columns for exporting
     * @param closure
     * @return
     */
    static eachColumn(GridConfig grid, boolean export = false, Closure closure) {
        grid.columns.findAll { col -> (params.selectionComp) ? col.showInSelection : true }
                .findAll { col -> !(export && col.export.hidden) }
                .eachWithIndex { col, idx ->
            switch (closure?.parameterTypes?.size()) {
                case 1:
                    return closure.call(col)
                case 2:
                    return closure.call(col, idx)
            }
        }
    }

    /**
     * returns the property type of a gorm domain class
     * @param grailsApplication
     * @param clazz
     * @param property
     * @return
     */
    static Class getPropertyType(GrailsApplication grailsApplication, Class clazz, String property) {
        getDomainProperty(grailsApplication, clazz, property)?.type
    }

    static GrailsDomainClass resolveDomainClass(grailsApplication, Class beanClass) {
        grailsApplication.getDomainClass(beanClass.name)
    }

    static GrailsDomainClassProperty getDomainProperty(GrailsApplication grailsApplication, Class clazz, String property) {
        resolveDomainClass(grailsApplication, clazz)?.getPropertyByName(property)
    }

    static Closure buildClosure(tkns, Closure last) {
        if (tkns.size() == 1) {
            return { filter ->
                "${tkns[-1]}"(last)
            }
        }
        return buildClosure(tkns[0..-2]) {
            "${tkns[-1]}"(last)
        }
    }

    @CompileStatic
    static String lastProperty(String property) {
        int idx = property.lastIndexOf('.')
        if (idx > -1) {
            return property[(idx + 1)..-1]
        }
        return property
    }


    static def convertValueUsingBinding(source, Class type) {
        def instance = new Object() {
            def errors = new Object()
        }
        addPropertyWithType(instance, 'x', type)
        DataBindingUtils.bindObjectToInstance(instance, [x: source])
        ValidationErrors errors = instance.errors
        if (errors.hasErrors()) {
            errors.allErrors[0]
            return null
        }
        instance.x
    }

    static private void addPropertyWithType(instance, prop, type) {
        instance.metaClass[prop] = new Object()
        instance.metaClass.expandoProperties.find { it.name == prop }.type = type
    }

//    @Memoized
    static boolean isControllerEasygridEnabled(grailsApplication, controllerName) {
        controllerName && grailsApplication.getArtefactByLogicalPropertyName(ControllerArtefactHandler.TYPE, controllerName)?.clazz?.isAnnotationPresent(Easygrid)
    }

    static def externalParams(gridConfig) {
        params.findAll { k, v ->k in gridConfig.externalParams }
    }
}
