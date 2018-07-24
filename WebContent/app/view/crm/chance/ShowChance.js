Ext.define('erp.view.crm.chance.ShowChance',{ 
	extend: 'Ext.Viewport', 
	autoScroll:true,
	layout:'column',
	style: 'background:#f1f1f1;',
	FormUtil: Ext.create('erp.util.FormUtil'),
	hideBorders: true, 
	//autoScroll:true,
	items:[{	
				height: '300',
	    	 	columnWidth:1,
	    	 	title:'商  机',
				xtype:'erpShowForm',
				caller:'Chance',
				winurl:'jsps/crm/chance/chance.jsp',
				keyF:'ch_id',
				page: 1,
				pageSize: 5,
				border: false
				
	     },{
	    	 	columnWidth:1,
	    	 	title:'报价情况明细',
				xtype:'erpDatalistGridPanel2',
				caller:'Quotation',
				codeF:'qu_code',
				keyF:'qu_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
	    	 	title:'送样情况明细',
				xtype:'erpDatalistGridPanel2',
				caller:'Sampleapply',
				codeF:'sa_code',
				keyF:'sa_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
	    	 	title:'解决方案',
				xtype:'erpDatalistGridPanel2',
				caller:'Solution',
				//condition:'',
				condition1:'',
				codeF:'so_chcode',
				keyF:'so_id',
				page: 1,
				pageSize: 5,
				border: false
	     },{
	    	 	columnWidth:1,
	    	 	title:'竞争对手',
				xtype:'erpDatalistGridPanel2',
				caller:'Competitor',
				//condition:'',
				condition1:'',
				codeF:'co_chcode',
				keyF:'co_id',
				page: 1,
				pageSize: 5,
				border: false
	     }],
		
	initComponent : function(){ 
		formCondition = getUrlParam('formCondition');//从url解析参数
    	formCondition = (formCondition == null) ? "" : formCondition.replace(/IS/g,"=");
    	console.log(formCondition);
		this.callParent(arguments); 
	} 
});