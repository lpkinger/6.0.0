Ext.define('erp.view.scm.sale.CalChargeBack',{ 
	extend: 'Ext.Viewport', 
	hideBorders: true, 
	layout: {
		type: 'vbox',
		align: 'center',
		pack: 'center'
	},
	initComponent : function(){ 
		var me = this; 
		var w = Ext.isIE ? screen.width*0.6*0.45 : '45%',
			h = Ext.isIE ? screen.height*0.7*0.45 : '45%';
		Ext.apply(me, { 
			items: [{
	    		width: 450,
				height: 300,
				bodyStyle: 'background: #f1f1f1;border:1px solid #cfcfcf',
				xtype: 'form',
				title: '借机扣款统计',
				layout: {
					type: 'vbox',
					align: 'center'
				},
		        items: [{	
		        	margin : '40 0 0 0',
			    	xtype: 'monthdatefield',
			    	fieldLabel: '日期',
			    	allowBlank: false,
			    	labelWidth: 60,
			    	id: 'date',
			    	name: 'date'
				}],
				bbar: {
					style:'border:1px solid #cfcfcf',
					items:['->',{
						xtype: 'erpConfirmButton',
						height: 26
					},{
						xtype:'erpCloseButton',
						height: 26
					},'->']
				}
			}] 
		}); 
		me.callParent(arguments); 
	} 
});