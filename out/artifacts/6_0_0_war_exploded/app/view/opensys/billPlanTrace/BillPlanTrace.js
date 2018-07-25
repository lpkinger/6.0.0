Ext.define('erp.view.opensys.billPlanTrace.BillPlanTrace',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	padding:'5 5 0 5',
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					region: 'north',
					xtype: 'erpQueryFormPanel',
					anchor: '100% 30%'
				},{
					xtype:'tabpanel',
					id:'tabpanel',
					anchor: '100% 70%',
					items:[{
						title:'订单进度',
						xtype: 'BillPlanTraceGrid',
						layout:'fit'
					},{
						title:'完工入库单',
						layout:'fit',
						listeners: {
                            activate: function (tab) {
                                var item = {
                                    xtype: 'ProdInOutMakeInGrid',
                                    layout:'fit'
                                };
                                tab.add(item);
                            }
                        }
					},{
						title:'订单视图',
						layout:'fit',
						listeners: {
                            activate: function (tab) {
                                var item = {
                                    xtype: 'OrderProcessChart',
                                    layout:'fit'
                                };
                                tab.add(item);
                            }
                        }
					}]
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});