Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.TenderPublic', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:['scm.sale.TenderPublic','scm.sale.TenderPublicFormPanel','scm.sale.TenderPublicGridPanel','core.button.Close','core.form.FileField2'],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpTenderPublicGridPanel': {
    			afterrender:function(grid){
    				Ext.Ajax.request({
			        	url : basePath + 'scm/sale/getTenderPublic.action',
			        	params: {
			        		id:id
			        	},
			        	method : 'post',
			        	callback : function(options,success,response){
			        		var res = new Ext.decode(response.responseText);
			        		if(res.exception || res.exceptionInfo){
			        			showError(res.exceptionInfo);
			        			return;
			        		}else{
				        		var form = grid.ownerCt.down('form');
				        		Ext.Array.each(form.items.items,function(field){
				        			if(typeof(field.setValue)=='function'){
				        				field.setValue(res.purchaseTender[field.name]);
				        			}
				        			
				        		});
				        		var bid = Ext.getCmp('bid');
				        		var mytender = Ext.getCmp('mytender');
				        		var turned = res.purchaseTender['turned'];
				        		var overdue = res.purchaseTender['overdue'];
				        		saleId = res.purchaseTender['saleId']
								if (turned ||overdue !=0) {
									bid.hide();
								}
								if(turned){
									mytender.show();
								}
								
				        		grid.store.loadData(res.purchaseTender.purchaseTenderProds);
				        		grid.store.sort({
									property: 'index',
									direction: 'ASC'
								});
			        		}
			        	}
    				});
    			}
    		},
    		'#bid' : {
				click : function(btn) {
					var me = this;
					var id = Ext.getCmp('id').value;
					me.FormUtil.setLoading(true);
					Ext.Ajax.request({
						url : basePath + 'scm/sale/addTenderItems.action',
						params : {id:id},
						method : 'post',
						callback : function(options,success,response){
							me.FormUtil.setLoading(false);
							var localJson = new Ext.decode(response.responseText);
							if(localJson.success){
								saleId = localJson.id;
								var code=Ext.getCmp('code').value;
								if(!Ext.isEmpty(saleId)){
									me.FormUtil.onAdd('TenderSubmission'+saleId, '投标单('+code+')', 'jsps/scm/sale/tenderSubmission.jsp?formCondition=idEQ'+saleId);
								}
								var mytender = btn.nextSibling('#mytender');
								mytender.show();
								btn.hide();
							} else if(localJson.exceptionInfo){
								var str = localJson.exceptionInfo;
								showError(str);
							}
						}
					});
				}
			},
			'#mytender' : {
				click : function(btn) {
					var code = Ext.getCmp('code').value;
					if(!Ext.isEmpty(saleId)){
						me.FormUtil.onAdd('TenderSubmission'+saleId, '投标单('+code+')', 'jsps/scm/sale/tenderSubmission.jsp?formCondition=idEQ'+saleId);
					}
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
    }
});