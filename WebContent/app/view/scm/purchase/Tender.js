Ext.define('erp.view.scm.purchase.Tender',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this,data = null;
		if(formCondition){
			data = me.getData();
		}
		Ext.apply(me, { 
			items: [{
				xtype: 'erpTenderFormPanel',
				anchor:'100% 45%',
				formdata:data?data.purchaseTender:null
			},{
				xtype: 'tabpanel',
				id:'tab',
				anchor:'100% 55%',
				items:[{
					title:'项目明细',
					xtype:'erpTenderProductGridPanel',
					griddata:data&&data.purchaseTender?data.purchaseTender.purchaseTenderProds:null
				},{
					title:'&nbsp;供&nbsp;应&nbsp;商&nbsp;',
					xtype:'erpTenderSupplierGridPanel',
					griddata:data?data.Vends:null,
					hidden:data?data.purchaseTender.ifOpen:true
				}]
			}]
		}); 
		me.callParent(arguments); 
	},
	getData: function(){
		var data = null;
		Ext.Ajax.request({
        	url : basePath + 'scm/purchase/getTender.action',
        	params: {id:id},
        	method : 'post',
        	async: false,
        	callback : function(options,success,response){
        		var res = new Ext.decode(response.responseText);
        		if(res.exception || res.exceptionInfo){
        			showError(res.exceptionInfo);
        			return;
        		}else{
        			if(res.purchaseTender&&res.purchaseTender.tt_statuscode&&res.purchaseTender.tt_statuscode=='AUDITED'&&window.location.search.indexOf('gridCondition')<0){
						window.location.href = 'tenderEstimate.jsp?formCondition=idIS' + id;
					}
        			data = res;
        			/*var main = parent.Ext.getCmp("content-panel") || parent.parent.Ext.getCmp("content-panel");
        			if(main){
        				
        			}*/
        		}
        	}
		});
		
		return data;
	}
});