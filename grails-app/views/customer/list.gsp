<%@page import="test.Customer"%>
<html>
	<head>
	<meta name="layout" content="main">
	<r:require modules="easygrid-jqgrid-dev,export"/>
		
	</head>
	<body>
		<div class="body">
			<h1>Customer List</h1>
			<div id='message' class="message" style="display:none;"></div>
			<table id="customer_list" class="scroll jqTable" cellpadding="0" cellspacing="0"></table>
			<div id="customer_list_pager" class="scroll" style="text-align:center;"></div>
			<div style="margin-top:5px;">
				<input class="ui-corner-all" id="btnAdd" type="button" value="Add Record"/>
				<input class="ui-corner-all" id="btnEdit" type="button" value="Edit Selected Record"/>
				<input class="ui-corner-all" id="btnDelete" type="button" value="Delete Selected Record"/>
			</div>
			
			<script type="text/javascript">
			var lastSelectedId;
			$(document).ready(function(){
				$("#btnAdd").click(function(){
					$("#customer_list").jqGrid("editGridRow","new",{addCaption:'Create New Customer',afterSubmit:afterSubmitEvent,savekey:[true,13]});
				});
				$("#btnEdit").click(function(){
					var gr = $("#customer_list").jqGrid('getGridRow','selrow');
					if(gr != null){
						$("#customer_list").jqGrid("editGridRow",gr,{closeAfterEdit:true,afterSubmit:afterSubmitEvent});
					}else
						alert("Please Select Row");
				});
				
				$("#btnDelete").click(function(){
					var gr = $("#customer_list").jqGrid('getGridRow','selrow');
					if(gr != null){
						$("#customer_list").jqGrid("delGridRow",gr,{afterSubmit:afterSubmitEvent});
					}else
						alert("Please Select Row to delete");
				});

				$("#customer_list").jqGrid({
					url:'jq_customer_list',
					editurl:'jq_edit_customer',
					datatype:"json",
					colNames:['First Name','Last Name','Age','Email Address','id'],
					colModel:[
						{name:'firstName',
						editable:true,
						editrules:{required:true}
						},
						{name:'lastName',
						editable:true,
						editrules:{required:true}
						},
						{name:'age',
						editable:true,
						editoptions:{size:3},
						editrules:{required:true,integer:true}
						},
						{name:'emailAddress',
						editable:true,
						editoptions:{size:30},
						editrules:{required:true,email:true}
						},
						{name:'id',hidden:true}	
					],
					rowNum:2,
					rowList:[1,2,3,4],
					pager:'#customer_list_pager',
					viewrecords:true,
					gridview:true
				}).navGrid('#customer_list_pager',{add:true,edit:true,del:true,search:false,refresh:true},
					{closeAfterEdit:true,afterSubmit:afterSubmitEvent},
					{addCaption:'Create New Customer',afterSubmit:afterSubmitEvent,savekey:[true,13]},
					{afterSubmit:afterSubmitEvent}
				);
				$("#customer_list").jqGrid('filterToolbar',{autosearch:true});
			});

			function afterSubmitEvent(response, postdata){
				var success = true;
				console.log('here')
				var json = eval('('+response.responseText+')');
				var message = json.message;
				if(json.state == 'FAIL'){
					success = false;
				}else{
					$('#message').html(message);
					$('#message').show.fadeOut(10000);
				}
				var new_id = json.id
				return [success,message,new_id];
			}
			</script>
		</div>
	</body>
</html>