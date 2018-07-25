Ext.define('erp.view.pm.WorkCenter', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	cls : 'x-mall',
	initComponent : function() {		
		var me = this;
		Ext.apply(me, {
			items : [{
				xtype:'grid',
				id : 'workcenter',
				autoScroll:true,
				region: 'center',
				columnLines : true,
				store: new Ext.data.Store({
			    	fields: [{
			    		name : 'WC_CODE',
			    		type : 'string'
			    	},{
			    		name : 'WC_NAME',
			    		type : 'string'
			    	}],
			    	data:[]
			    }),
				columns : [{
				    text : '工作中心',
				    dataIndex : 'WC_CODE',
				    align : 'left',
				    width : 0
				},{
					text : '工作中心',
					dataIndex : 'WC_NAME',
					aligh : 'left',
					width : 120
				}],
				listeners: {//滚动条有时候没反应，添加此监听器
					scrollershow: function(scroller) {
						if (scroller && scroller.scrollEl) {
							scroller.clearManagedListeners();  
							scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
						}
					}
	    		}
			}]
		})
		me.callParent(arguments);
	}
	});