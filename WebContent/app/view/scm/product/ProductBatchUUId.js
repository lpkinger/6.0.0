Ext.define('erp.view.scm.product.ProductBatchUUId',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{					
					xtype: 'erpGridPanel2',
					anchor: '100% 100%', 
					detno: 'pub_detno',
					keyField: 'pub_id',
					headerCt: Ext.create("Ext.grid.header.Container",{
					 	    forceFit: false,
					        sortable: true,
					        enableColumnMove:true,
					        enableColumnResize:true,
					        enableColumnHide: true
					     }),
					 dockedItems: [{
					 	xtype: 'toolbar',
					 	padding:'8 5 8 55',	
					 	dock: 'top',
					 	border:false,
					 	items: [{
				            xtype: 'tbtext', 
				            text: '<span style="color: gray;">--选择物料后系统会根据"原厂型号"自动匹配，匹配不上的可通过下方按钮“手工匹配”进行手工匹配</span>'
				        }]
					 },{
				        xtype: 'toolbar',
				        dock: 'top',
				        padding:'8 5 8 20',	
				        border:false,
				        items: [{
				            xtype:'erpLoadProdButton'
				        },'-',{
				        	xtype:'erpRemoveUUIdButton'
				        }]
				    }],
					selModel: Ext.create('Ext.selection.CheckboxModel',{}),					
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
				    sync: true				   
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});