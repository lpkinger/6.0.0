Ext.define('erp.view.crm.customercare.ShowCustomer',{ 
	extend: 'Ext.Viewport', 
	autoScroll:true,
	layout:'column',
	style: 'background:#f1f1f1;',
	hideBorders: true, 
	formCondition:'',
	//autoScroll:true,
	items:[{
			height: '300',
			layout:'border',
			columnWidth:1,
			items:[{
				width:'60%', 
				region:'center',
				height: '100',
				title:'基本资料',
				xtype:'erpShowForm',
				caller:'ShowCustomer',
				winurl:'jsps/drp/distribution/customerType.jsp',
				keyF:'cu_code',
				page: 1,
				pageSize: 5,
				border: false
			},{ 
				width:'40%', 
				region:'east',
				layout:'column',
				items:[{
					xtype:'splitter',
					columnWidth:.03
				},{
					/*columnWidth:.97,
					height: '300',
					html:'<iframe  width=100% height=100% src="../../common/datalist.jsp?whoami=Contact"/>'*/
					columnWidth:.97,
					height: '300',
					xtype:'erpDatalistGridPanel2',
					caller:'Contact',
					keyF:'ct_id',
					codeF:'ct_cucode',
					page: 1,
					pageSize: 5
				}]
			}]
			
	     },{	
	    	 	columnWidth:1,
	    	 	title:'拜访计划',
				xtype:'erpDatalistGridPanel2',
				caller:'CallPlan',
				condition1:'',
				keyF:'cp_id',
				codeF:'cp_cucode',
				page: 1,
				pageSize: 5,
				border: false
				
	     },{	
	    	 	columnWidth:1,
	    	 	title:'拜访记录',
				xtype:'erpDatalistGridPanel2',
				caller:'VisitRecord',
				condition1:'',
				keyF:'vr_id',
				codeF:'vr_cuuu',
				page: 1,
				pageSize: 5,
				border: false
				
	     },{
	    	 	columnWidth:1,
				html:'<div style="text-align:center;font-weight:bold">  -售    前-  </div>',
	     		height: '22'
	     },{
	    	 	columnWidth:1,
	    	 	title:'商  机',
				xtype:'erpDatalistGridPanel2',
				caller:'Chance!Query',
				//condition:'',
				//condition1:'',
				keyF:'ch_id',
				codeF:'ch_cucode',
				page: 1,
				pageSize: 5,
				border: false
				
	     },{
	    	 	columnWidth:1,
	    	 	title:'报  价',
				xtype:'erpDatalistGridPanel2',
				caller:'Quotation',
				//condition:'',
				//condition1:'',
				keyF:'qu_id',
				codeF:'qu_custcode',
				page: 1,
				pageSize: 5,
				border: false
				
	     },{
	    	 	columnWidth:1,
				html:'<div style="text-align:center;font-weight:bold">  -售    中-  </div>',
	     		height: '22'
	     },{
	    	 	columnWidth:1,
	    	 	title:'订  单',
				xtype:'erpDatalistGridPanel2',
				caller:'Sale',
				//condition:'',
				//condition1:'',
				codeF:'sa_custcode',
				keyF:'sa_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
	    	 	title:'收款计划',
				xtype:'erpDatalistGridPanel2',
				caller:'CollectionPlan',
				//condition:'',
				//condition1:'',
				codeF:'cp_cucode',
				keyF:'cp_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
				html:'<div style="text-align:center;font-weight:bold">  -售    后-  </div>',
	     		height: '22'
	     },{
	    	 	columnWidth:1,
	    	 	title:'客户投诉',
				xtype:'erpDatalistGridPanel2',
				caller:'Complaint',
				codeF:'co_cucode',
				keyF:'co_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
	    	 	title:'保修单',
				xtype:'erpDatalistGridPanel2',
				caller:'CustomerRepair',
				codeF:'cr_cuname',
				keyF:'cr_id',
				page: 1,
				pageSize: 5,
				border: false
	     }
		],
	initComponent : function(){
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
		this.callParent(arguments); 
	} 
});