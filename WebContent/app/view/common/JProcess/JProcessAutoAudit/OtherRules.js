
Ext.define('erp.view.common.JProcess.JProcessAutoAudit.OtherRules',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.OtherRules',
	id: 'otherrules', 
	autoScroll:true,
	closeAction:'hide',
	layout: "anchor",	
	items:[{
		xtype:'grid',
		id:'otherrulesgrid',	
		height:200,
		/*selModel:  Ext.create('Ext.selection.CheckboxModel', {
					checkOnly:true,
					mode :'SINGLE'
				}),*/
		columns:[		
			  { header: '流程信息', 
			  	dataIndex: '' ,
			  	flex: 2,
			  	renderer:function(val,etc,record){
			  		return record.data.JD_PROCESSDEFINITIONID+' / '+record.data.JT_NAME;			  	
			  	}			  
			  },
        	  { header: '规则信息', dataIndex: 'RA_RULEDESC', flex: 4,
        	  	renderer:function(val,etc,record){
        	  		var ra_rulename=record.data.RA_RULENAME!=""?record.data.RA_RULENAME+" / ":"";
        	  		return ra_rulename+val;
        	  	}
        	  },
        	  { header: '是否启用', dataIndex: 'RA_STATUS', flex: 0.5 ,
        	  	renderer:function(val,etc,record){
        	  		if(val=='已提交'){
		        	  	return '审核中'
		        	}else if(val=='已审核'){
		        	  	return '已启用' 
		        	 }else{
		        	  	return val;
		        	  	}	        	  	
        	  	}        	  
        	  },
        	  { header: '申请历史', dataIndex: '', flex: 0.5,
        	  	renderer:function(val,etc,record){
        	  		var data=record.data;
        	  		var caller=data.JD_CALLER;
        	  		var nodename=data.JT_NAME;
        	  		var code=data.RA_CODE;
        	  		return '<a href="javascript:applyHistory(\''+caller+'\',\''+nodename+'\',\''+code+'\')">明细</a>'       	  	
        	  	}
        	  }        	  
		],
		store :Ext.create('Ext.data.Store', {
	 			fields: [{name: 'JD_CALLER', type: 'string'},
						 {name: 'JD_PROCESSDEFINITIONID', type: 'string'},
						 {name: 'JT_ASSIGNEE', type: 'string'},
						 {name: 'JT_NAME', type: 'string'},
					 	 {name: 'JT_ROLES', type: 'string'},
					 	 {name: 'RA_RULEDESC', type: 'string'},
					 	 {name: 'RA_CODE', type: 'string'},
					 	 {name: 'RA_STATUS', type: 'string'},
					 	 {name: 'RA_RULENAME', type: 'string'}
					 	 ],
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : basePath + 'common/getOtherRulesData.action',					
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'total'
					},
					actionMethods: {
	            			read   : 'POST'
	       	 		}
				},
				listeners : {
					datachanged : function(store) {
						
						console.log("888888");
						/*
						
						var otherrulesgrid=Ext.getCmp("otherrulesgrid");
						var flowbody=parent.Ext.getCmp('flowbody');
						var nodename=flowbody.currentnode;
						var caller=flowbody.caller;
						console.log(store);
						Ext.each(store.data.items,function(i,index){
							if(i.data.JD_CALLER==caller && i.data.JT_NAME==nodename){
								otherrulesgrid.getSelectionModel().select(index);
							}						
						});
						
					
					*/}
				}
	 			})
	}],	
	initComponent : function(){ 	
		this.callParent(arguments);
		var me=this;
		//me.getOtherRulesData();
	},
	getOtherRulesData:function(){
		Ext.Ajax.request({
			url: basePath + 'common/getOtherRulesData.action',
			params: {
				_noc: 1
			},
			callback: function(options, success, response) {
				var response = Ext.decode(response.responseText);	
				var otherrulesgrid=Ext.getCmp("otherrulesgrid");
				var flowbody=parent.Ext.getCmp('flowbody');
				var nodename=flowbody.currentnode;
				var caller=flowbody.caller;
				otherrulesgrid.store.loadData(response);
				Ext.each(response,function(i,index){
					if(i.JD_CALLER==caller && i.JT_NAME==nodename){
						otherrulesgrid.getSelectionModel().select(index);
					}						
				});
			}
		});		
	}	
});

var applyHistory=function(caller,nodename,code){
	var win = new Ext.window.Window({
		title: '申请历史',
    	id : 'win',
		height: "50%",
		width: "98%",
		draggable :false,
		resizable :false,
		layout:'fit',
		style:{
			'background':'#ffffff'
			},
		bodyStyle:{
			'background':'#ffffff'
		},
		 items: [{
		 			xtype:'grid',
		 			id:'historygrid',
		 			autoScroll:true,
		 			caller:caller,
		 			nodename:nodename,
		 			code:code,
		 			style:{
						'background':'#ffffff'
					},
		 			columns:[
		 				  { header: '',  dataIndex: '' ,flex: 1,
		 				  	renderer:function(val,etc,record){
		 				  		return record.data.RA_TYPE+" / "+record.data.RA_RECORDER+" / "+record.data.RA_DATE;
		 				  	}		 				  
		 				  },
		 			  	  { header: '',  dataIndex: 'RA_CODE' ,flex: 0.7,
		 			  	  	renderer:function(val,etc,record){
		 			  	  		var ra_id=record.data.RA_ID;
		 			  	  		var url="jsps/common/jprocessDeal/jprocessRulesApply.jsp?whoami=JprocessRulesApply&formCondition=ra_idIS"+ra_id;
		 			  	  		return '<a href="javascript:openUrl(\''+url+'\')">'+val+'</a>';
		 			  	  	
		 			  	  	}
		 			  	  
		 			  	  },
		 			  	  { header: '',  dataIndex: '' ,flex: 2,
		 			  	  	renderer:function(val,etc,record){
		 			  	  		var em_name =record.data.EM_NAME.length==0?"":record.data.EM_NAME+" / ";
		 			  	  		var jp_nodename=record.data.JP_NODENAME.length==0?"":record.data.JP_NODENAME+" / ";
		 			  	  		var jp_status=record.data.JP_STATUS.length==0?"":record.data.JP_STATUS;
		 			  	  		return   em_name+jp_nodename+jp_status;
		 			  	  	 
		 			  	  	}
		 			  	  
		 			  	  },
		 			  	  { header: '',  dataIndex: 'RA_STATUS' ,flex: 0.5,
		 			  	  	renderer:function(val,etc,record){
		 				  		if(val=='已提交'){
		 				  			return "受理中";
		 				  		}else if(val=="已审核"){
		 				  			return "已受理";
		 				  		}else if(val=="禁用"){
		 				  			return "已禁用";
		 				  		}else{
		 				  			return val;
		 				  		}
		 				  	}
		 			  	  }
		 			],
		 			store :Ext.create('Ext.data.Store', {
				 			fields: [
				 				{name: 'RA_ID', type: 'string'},
				 				{name: 'RA_TYPE', type: 'string'},
								{name: 'RA_STATUS', type: 'string'},
								{name: 'RA_RECORDER',type: 'string'},
								{name: 'EM_NAME', type:'string'},
								{name: 'JP_NODENAME',type: 'string'},
								{name: 'JP_STATUS',type: 'string'},
								{name: 'RA_DATE', type: 'string'},
								{name: 'RA_CODE', type: 'string'}]
								
								  
			 			})	 		
		 		}
		 ] 
	});
	win.show();
}
