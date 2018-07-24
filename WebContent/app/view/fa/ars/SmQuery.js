Ext.define('erp.view.fa.ars.SmQuery', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [ {
				xtype: 'erpGridPanel2',
				bbar: null,
				style:{background:'#fff'},
				tbar: {style:{background:'#fff'},margin:'0 0 5 0',items:[{
		    		name: 'query',
		    		text: $I18N.common.button.erpQueryButton,
		    		iconCls: 'x-button-icon-query',
		        	cls: 'x-btn-gray'
		    	},{
		    		margin:'0 0 0 5',
		    		name: 'refresh',
		    		text: $I18N.common.button.erpRefreshButton,
		    		iconCls: 'x-button-icon-reset',
		        	cls: 'x-btn-gray'
		    	},{
		    		margin:'0 0 0 5',
					name: 'export',
					text: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
			    	cls: 'x-btn-gray'
				},{
					margin:'0 0 0 5',
					name: 'print',
					text: $I18N.common.button.erpPrintButton,
			    	iconCls: 'x-button-icon-print',
			    	cls: 'x-btn-gray'
				},{
		    		margin:'0 0 0 5',
		    		xtype: 'tbtext',
		    		name: 'info',
		    		tpl: Ext.create('Ext.XTemplate',
		    				'<span>{cm_yearmonth:this.getYearmonth}</span>', {
		    			getYearmonth: function(v) {
		    				if(v instanceof Object) {
		    					var v1 = String(v.begin), v2 = String(v.end);
		    					return ' 期间: 从' + v1.substr(0, 4) + '年' + v1.substr(4) + '月 到 ' +
		    						v2.substr(0, 4) + '年' + v2.substr(4) + '月';
		    				}
		    			}
		    		})
		    	},'->',{
					text: $I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					id:'close',
			    	cls: 'x-btn-gray',
			    	handler: function(){
			    		var main = parent.Ext.getCmp("content-panel"); 
			    		main.getActiveTab().close();
			    	}
				}]},
				setLoading : function(b) {
					var mask = this.mask;
					if (!mask) {
						this.mask = mask = new Ext.LoadMask(Ext.getBody(), {
							msg : "处理中,请稍后...",
							msgCls : 'z-index:10000;'
						});
					}
					if (b)
						mask.show();
					else
						mask.hide();
				},
				cls: 'custom-grid',
				viewConfig: { 
			        getRowClass: function(record) {
			        	var s = null, t = record.get('sm_showtype');
			        	switch(t) {
				        	case '1':
				        		s = 'custom';break;
				        	case '2':
				        		s = 'custom';break;
				        	case '3':
				        		s = null;break;
				        	case '4':
				        		s = null;break;
				        	case '5':
				        		s = null;break;
			        	}
			            return s; 
			        } 
			    }
			} ]
		});
		me.callParent(arguments);
	}
});