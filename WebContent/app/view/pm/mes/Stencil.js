Ext.define('erp.view.pm.mes.Stencil',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'StencilViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 40%',
					saveUrl: 'pm/mes/saveStencil.action',
					deleteUrl: 'pm/mes/deleteStencil.action',
					updateUrl: 'pm/mes/updateStencil.action',
					getIdUrl: 'common/getId.action?seq=Stencil_SEQ',
					submitUrl: 'pm/mes/submitStencil.action',
					auditUrl: 'pm/mes/auditStencil.action',
					resAuditUrl: 'pm/mes/resAuditStencil.action',			
					resSubmitUrl: 'pm/mes/resSubmitStencil.action',
					keyField: 'st_id',
					codeField: 'st_code', 
					statusField: 'st_status',
					statuscodeField: 'st_statuscode'
				},{				
					title:'使用登记',
					xtype : 'erpGridPanel2',
					anchor : '100% 60%',
					keyField : 'su_id',
					mainField : 'su_stid',
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
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});