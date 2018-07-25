Ext.define('erp.view.common.bench.ResultGridPanel',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpResultGridPanel',
	id:'resultGrid',
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: [],
    columns: [],
    bbar:['->',{xtype: 'tbtext',text: '共: 0  条, 已选: 0  条'}],
    constructor: function(cfg) {
		if(cfg) {
			cfg.plugins = cfg.plugins || [Ext.create('Ext.ux.grid.GridHeaderFilters'), Ext.create('erp.view.core.plugin.CopyPasteMenu')];
	    	Ext.apply(this, cfg);
		}
		this.callParent(arguments);
	},
	initComponent : function(){ 
		Ext.apply(this, { 
			selModel: Ext.create('Ext.selection.CheckboxModel',{
		   		checkOnly:true,
				listeners: {
					selectionchange: function(selModel, selected, eOpts ){
						var grid = selModel.view.ownerCt;
						if(grid.selectAll){
							if(selected.length==0){
			            		Ext.Array.each(grid.store.data.items,function(deselect){
		            				var d=deselect.data;
		            				delete d.RN;
		            				var name = "";
		            				if(grid.keyField != null && grid.keyField != ''){
						    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
						    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(d);//vd_vsid@vd_id+vd_class@vd_class
										   	Ext.Array.each(arr, function(r){
											   ff = r.split('@');
											   if(fields.indexOf(ff[1]) > -1) {
												   val = d[ff[1]];
									    		   if(val instanceof Date)
									    			   val = Ext.Date.format(val, 'Y-m-d');
											   } else {
												   val = ff[1];
											   }
											   name += val;
										   });
						    		   	} else {
						    		   		name = d[grid.keyField];
						    		   	}
						    		   	delete grid.selectObject[name];
						        	}
		            			});
			            	}else if(selected.length==grid.store.getCount()){
			            		Ext.Array.each(selected,function(select){
			            			var d=select.data;
		            				delete d.RN;
		            				var name = "";
		            				if(grid.keyField != null && grid.keyField != ''){
						    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
						    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(d);//vd_vsid@vd_id+vd_class@vd_class
										   	Ext.Array.each(arr, function(r){
											   ff = r.split('@');
											   if(fields.indexOf(ff[1]) > -1) {
												   val = d[ff[1]];
									    		   if(val instanceof Date)
									    			   val = Ext.Date.format(val, 'Y-m-d');
											   } else {
												   val = ff[1];
											   }
											   name += val;
										   });
						    		   	} else {
						    		   		name = d[grid.keyField];
						    		   	}
						    		   	grid.selectObject[name]=d;
						        	}
		            			});
			            	}
			            	grid.updateInfo();
			            	grid.selectAll=false;
						}
					},
		            select:function(selModel, record, index, opts){//选中
		            	var grid=selModel.view.ownerCt;
		            	if(!grid.selectAll){
			            	var d=record.data;
				            delete d.RN;
				            var name = "";
			            	if(grid.keyField != null && grid.keyField != ''){
				    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
				    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(record.data);//vd_vsid@vd_id+vd_class@vd_class
								   	Ext.Array.each(arr, function(r){
									   ff = r.split('@');
									   if(fields.indexOf(ff[1]) > -1) {
										   val = record.get(ff[1]);
							    		   if(val instanceof Date)
							    			   val = Ext.Date.format(val, 'Y-m-d');
									   } else {
										   val = ff[1];
									   }
									   name += val;
								   });
				    		   	} else {
				    		   		name = d[grid.keyField];
				    		   	}
					           	grid.selectObject[name]=d;
				        	}
				        	grid.updateInfo();
		            	}
		            },
		            deselect:function(selModel, record, index, opts){//取消选中
		            	var grid=selModel.view.ownerCt;
		            	if(!grid.selectAll){
			            	var d=record.data;
				            delete d.RN;
				            var name = "";
			            	if(grid.keyField != null && grid.keyField != ''){
				    		   	if(grid.keyField.indexOf('+') > 0) {//多条件传入查询界面//vd_vsid@vd_id+vd_class@vd_class
				    		   		var arr = grid.keyField.split('+'), ff = [], val, fields = Ext.Object.getKeys(record.data);//vd_vsid@vd_id+vd_class@vd_class
								   	Ext.Array.each(arr, function(r){
									   ff = r.split('@');
									   if(fields.indexOf(ff[1]) > -1) {
										   val = record.get(ff[1]);
							    		   if(val instanceof Date)
							    			   val = Ext.Date.format(val, 'Y-m-d');
									   } else {
										   val = ff[1];
									   }
									   name += val;
								   });
				    		   	} else {
				    		   		name = d[grid.keyField];
				    		   	}
				    		   	delete grid.selectObject[name];
				        	}
				        	grid.updateInfo();
		            	}
		            }
		        },
		        onHeaderClick : function(b, d, a) {
					if (d.isCheckerHd) {
						var grid = b.ownerCt;
		        		grid.selectAll = true;
						a.stopEvent();
						var c = d.el.hasCls(Ext.baseCSSPrefix
								+ "grid-hd-checker-on");
						if (c) {
							this.deselectAll(true)
						} else {
							this.selectAll(true)
						}
					}
		        }
			})
		});
		this.callParent(arguments);
	},
	updateInfo:function(){
		var grid=this;
		var count_all=grid.store.data.items.length;
		var count_select=grid.selModel.getSelection().length;
		var count = grid.down('toolbar').down('tbtext');
		if (count) count.setText('共: ' + count_all + ' 条, 已选: ' + count_select+ ' 条');
	},
	listeners: {
		'headerfiltersapply': function(grid, filters,active, store) {
			grid.selectAll=true;
		}
	}
});