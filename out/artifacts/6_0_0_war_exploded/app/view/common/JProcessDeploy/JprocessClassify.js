Ext.define('erp.view.common.JProcessDeploy.JprocessClassify',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'fit', 
				items: [{ 	
					xtype: 'erpGridPanel2',
					anchor:'100% 100%',
					autoScroll : true,
					height:height,//坑爹的样式
					layout : 'fit',
					region:'center',
					bodyStyle: 'background-color:#f1f1f1;',				   
					plugins: [Ext.create('erp.view.core.grid.HeaderFilter')],
					condition:"1=1",
					headerCt: Ext.create("Ext.grid.header.Container",{
				 	    forceFit: false,
				        sortable: true,
				        enableColumnMove:true,
				        enableColumnResize:true,
				        enableColumnHide: true
				     }),
				    invalidateScrollerOnRefresh: false,
				    viewConfig: {
				        trackOver: false
				    },
				    buffered: true,
				    sync: true,
					multiselected: [],
					  selModel: Ext.create('Ext.selection.CheckboxModel',{
					    	ignoreRightMouseSelection : false,
							listeners:{
					            selectionchange:function(selectionModel, selected, options){
					          
					            }
					        },
					        getEditor: function(){
					        	return null;
					        },
					        onRowMouseDown: function(view, record, item, index, e) {//改写的onRowMouseDown方法
					        	var me = Ext.getCmp('grid');
					        	var bool = true;
					        	var items = me.selModel.getSelection();
					            Ext.each(items, function(item, index){
					            	if(this.index == record.index){
					            		bool = false;
					            		me.selModel.deselect(record);
					            		Ext.Array.remove(items, item);
					            		Ext.Array.remove(me.multiselected, record);
					            	}
					            });
					            Ext.each(me.multiselected, function(item, index){
					            	items.push(item);
					            });
					        	if(bool){
					        		view.el.focus();
						        	var checkbox = item.childNodes[0].childNodes[0].childNodes[0];
						        	if(checkbox.getAttribute && checkbox.getAttribute('class') == 'x-grid-row-checker'){
						        		me.multiselected.push(record);
						        		items.push(record);
						        		me.selModel.select(me.multiselected);
						        	} else {
						        		me.selModel.deselect(record);
						        		Ext.Array.remove(me.multiselected, record);
						        	}
					        	}
					        	if(items.length>0){						       
					        		Ext.getCmp('moveto').setDisabled(false);
					        	}else {
					        	Ext.getCmp('moveto').setDisabled(true);
					        	}
					        },
					        onHeaderClick: function(headerCt, header, e) {
					            if (header.isCheckerHd) {
					                e.stopEvent();
					                var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
					                if (isChecked) {
					                    this.deselectAll(true);
					                    var grid = Ext.getCmp('grid');
					                    this.deselect(grid.multiselected);
					                    grid.multiselected = new Array();
					                    var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
						                Ext.each(els, function(el, index){
						                	el.setAttribute('class','x-grid-row-checker');
						                });
					                    header.el.removeCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
					                    Ext.getCmp('moveto').setDisabled(true);
					                } else {
					                	var grid = Ext.getCmp('grid');
					                	this.deselect(grid.multiselected);
						                grid.multiselected = new Array();
						                var els = Ext.select('div[@class=x-grid-row-checker-checked]').elements;
						                Ext.each(els, function(el, index){
						                	el.setAttribute('class','x-grid-row-checker');
						                });
					                    this.selectAll(true);
					                    Ext.getCmp('moveto').setDisabled(false);
					                    header.el.addCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');//添加这个
					                }
					            }
					        }
					    }),
					    getMultiSelected: function(){
							var grid = this;
					        var items = grid.selModel.getSelection();
					        Ext.each(items, function(item, index){
					        	if(this.data[grid.keyField] != null && this.data[grid.keyField] != ''
					        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
					        		grid.multiselected.push(item);
					        	}
					        });
							var records=Ext.Array.unique(grid.multiselected);
						      	var params = new Object();
								params.caller = caller;
								var data = new Array();
								Ext.each(records, function(record, index){
									if(grid.keyField && this.data[grid.keyField] != null && this.data[grid.keyField] != ''
						        		&& this.data[grid.keyField] != '0' && this.data[grid.keyField] != 0){
										bool = true;
										var o = new Object();
										o[grid.keyField] = record.data[grid.keyField];
										if(grid.necessaryFields){
											Ext.each(grid.necessaryFields, function(f, index){
												var v = record.data[f];
												if(Ext.isDate(v)){
													v = Ext.Date.toString(v);
												}
												o[f] = v;
											});
										}
										data.push(o);
									}
								});
						        params.data = Ext.encode(data);
						        return params;
						}
			}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});