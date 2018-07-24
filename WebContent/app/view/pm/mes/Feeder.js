Ext.define('erp.view.pm.mes.Feeder',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'FeederViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'pm/mes/saveFeeder.action',
					deleteUrl: 'pm/mes/deleteFeeder.action',
					updateUrl: 'pm/mes/updateFeeder.action',
					getIdUrl: 'common/getId.action?seq=Feeder_SEQ',
					submitUrl: 'pm/mes/submitFeeder.action',
					auditUrl: 'pm/mes/auditFeeder.action',
					resAuditUrl: 'pm/mes/resAuditFeeder.action',			
					resSubmitUrl: 'pm/mes/resSubmitFeeder.action',
					keyField: 'fe_id',
					codeField: 'fe_code', 
					statusField: 'fe_status',
					statuscodeField: 'fe_statuscode'
				},{					
					xtype:'tabpanel',
					id:'tab',
					anchor: '100% 65%', 
					items:[{
						xtype: 'erpGridPanel2',
						title:'保养维修记录',
						anchor: '100% 65%', 
						keyField: 'fl_id',
						mainField: 'fl_feid',
						id: 'grid1',
						headerCt: Ext.create("Ext.grid.header.Container",{
						 forceFit: false,
						 sortable: true,
						 enableColumnMove:true,
						 enableColumnResize:true,
						 enableColumnHide: true
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
						    	                    		//**字符串转换下简体*//*
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
						    sync: true		
					},{
						title:'使用记录',
						items: [],
						layout: 'anchor',
						id: 'tab-list'
					}]			
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});