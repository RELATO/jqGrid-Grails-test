package test

import grails.converters.JSON

class CustomerController {
	static allowedMethods = [save:"POST", update:"POST", delete:"POST"]
	
    def index() { 
		redirect(action:"list",params:params)
	}
	
	def list = {
		params.max = Math.min(params.max?params.int('max'):10, 100)
		[customerInstanceList:Customer.list(params), customerInstanceTotal:Customer.count()]
	}
	
	//return JSON list of customers
	def jq_customer_list = {
		
		def sortIndex = params.sidx ?: 'lastName'
		def sortOrder = params.sord ?: 'asc'
		
		def maxRows = Integer.valueOf(params.rows)
		def currentPage = Integer.valueOf(params.page) ?: 1
		def rowOffset = currentPage == 1 ? 0 : (currentPage - 1) * maxRows
		
		def customers = Customer.createCriteria().list(max:maxRows,offset:rowOffset){
			if(params.firstName)
				ilike('firstName',params.firstName+'%')
			if(params.lastName)
				ilike('lastName',params.lastName+'%')
			if(params.age)
				eq('age',Integer.valueOf(params.age))
			if(params.emailAddress)
				ilike('emailAddress',params.emailAddress+'%')
			order(sortIndex, sortOrder)
			//ignoreCase()
		}
		
		//System.out.println("Teste aqui customers:"+customers);
		
		def totalRows = customers.totalCount
		def numberOfPages = Math.ceil(totalRows / maxRows)
		
		def jsonCells = customers.collect {
			[cell: [it.firstName,
					it.lastName,
					it.age,
					it.emailAddress
				], id:it.id]
		}
		def jsonData = [rows: jsonCells, page:currentPage,records:totalRows,total:numberOfPages]
		render jsonData as JSON
	}
	
	def jq_edit_customer = {
		def customer = null
		def message = ""
		def state = "FAIL"
		def id
		
		switch (params.oper){
			case 'add':
				customer = new Customer(params)
				if(!customer.hasErrors() && customer.save()){
					message = "Customer ${customer.firstName} ${customer.lastName} Added"
					id = customer.id
					state = "OK"
				}else{
					message = "Could not save customer"
				}
				break;
			case 'del':
				customer = Customer.get(params.id)
				if(customer){
					customer.delete()
					message = "Customer ${customer.firstName} ${customer.lastName} Deleted"
					state = "OK"
				}
				break;
			default:
				customer = Customer.get(params.id)
				if(customer){
					customer.properties = params
					if(! customer.hasErrors() && customer.save()){
						message = "Customer ${customer.firstName} ${customer.lastName} Updated "
						id = customer.id
						state = "OK"
					}else{
						message = "Could Not Update Customer"
					}
				}
				break;
		}
		def response = [message:message,state:state,id:id]
		render response as JSON
	}
}
