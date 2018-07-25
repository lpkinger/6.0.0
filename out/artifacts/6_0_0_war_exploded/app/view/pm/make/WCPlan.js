Ext.define('erp.view.pm.make.WCPlan',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout:'anchor',
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'pm/make/saveWCPlan.action',
					deleteUrl: 'pm/make/deleteWCPlan.action',
					updateUrl: 'pm/make/updateWCPlan.action',
					submitUrl: 'pm/make/submitWCPlan.action',
					auditUrl: 'pm/make/auditWCPlan.action',
					resAuditUrl: 'pm/make/resAuditWCPlan.action',					
					resSubmitUrl: 'pm/make/resSubmitWCPlan.action',
					getIdUrl: 'common/getId.action?seq=WCPLAN_SEQ',
					runLackMaterialUrl:'pm/make/RunLackMaterial.action',
					keyField: 'wc_id',
					statusField: 'wc_status',
					codeField: 'wc_statuscode'
				},{
					anchor: '100% 65%',
					xtype:'erpGridPanel2',
					headerCt: Ext.create("Ext.grid.header.Container",{
					 	    forceFit: false,
					        sortable: true,
					        enableColumnMove:true,
					        enableColumnResize:true,
					        enableColumnHide: true
					     }),
					selModel: Ext.create('Ext.selection.CheckboxModel',{
						headerWidth: 0
					}),
					plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu'),Ext.create('erp.view.core.grid.HeaderFilter')],
				    invalidateScrollerOnRefresh: false,	
				    listeners: {
				        'headerfiltersapply': function(grid, filters) {
				        	if(this.allowFilter){
				        		var condition = null;
				                for(var fn in filters){
				                    var value = filters[fn],f = grid.getHeaderFilterField(fn);
				                    if(!Ext.isEmpty(value)){
				                    	if(f.filtertype) {
				                    		if (f.filtertype == 'numberfield') {
				                    			value = fn + "=" + value + " ";
				                    		}
				                    	} else {
				                    		if(Ext.isDate(value)){
				                        		value = Ext.Date.toString(value);
				                        		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
				                        	} else {
				                        		var exp_t = /^(\d{4})\-(\d{2})\-(\d{2}) (\d{2}):(\d{2}):(\d{2})$/,
				                        		exp_d = /^(\d{4})\-(\d{2})\-(\d{2})$/;
				    	                    	if(exp_d.test(value)){
				    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value + "' ";
				    	                    	} else if(exp_t.test(value)){
				    	                    		value = "to_char(" + fn + ",'yyyy-MM-dd')='" + value.substr(0, 10) + "' ";
				    	                    	} else{
				    	                    		if (f.xtype == 'combo' || f.xtype == 'combofield') {
				    	                    			if (value == '-所有-') {
				    	                    				continue;
				    	                    			} else if (value == '-无-') {
				    	                    				value = 'nvl(' + fn + ',\' \')=\' \'';
				    	                    			} else {
				    	                    				value = fn + " LIKE '" + value + "%' ";
				    	                    			}
				    	                    		} else {
				    	                    			/**字符串转换下简体*/
				    	                    			var SimplizedValue=this.BaseUtil.Simplized(value);   	                    	
				    	                    			//可能就是按繁体筛选  
				    	                    			if(f.ignoreCase) {// 忽略大小写
				        	                    			fn = 'upper(' + fn + ')';
				        	                    			value = value.toUpperCase();
				        	                    		}
				        	                    		if(!f.autoDim) {
				        	                    			if(SimplizedValue!=value){
				        	                    				value = "("+fn + " LIKE '" + value + "%' or "+fn+" LIKE '"+SimplizedValue+"%')";
				        	                    			}else value = fn + " LIKE '" + value + "%' ";       	                    			
				        	                    			
				        	                    		} else if(f.exactSearch){
				        	                    			value=fn+"='"+value+"'";
				        	                    		}else {
				        	                    			if(SimplizedValue!=value){
				        	                    				value = "("+fn + " LIKE '%" + value + "%' or "+fn+" LIKE '%"+SimplizedValue+"%')";
				        	                    			}else value = fn + " LIKE '%" + value + "%' ";       	                    			        	                    			
				        	                    		}
				    	                    		}
				    	                    	}
				                        	}
				                    	}
				                    	if(condition == null){
				                    		condition = value;
				                    	} else {
				                    		condition = condition + " AND " + value;
				                    	}
				                    }
				                }
				                this.filterCondition = condition;
				                var grid=Ext.getCmp('grid');
				                grid.store.remoteSort=true;
				                if(grid.store.data!=grid.store.prefetchData){
				                	grid.store.loadData(grid.store.prefetchData.items);
				                }
				        	} else {
				        		this.allowFilter = true;
				        	}
				        	return false;
				        }
				    },
				    viewConfig: {
				        trackOver: false
				    },
				    buffered: true,
				    sync: true,
					region:'center'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});