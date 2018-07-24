/**
 * 浮动窗口
 */
Ext.define('erp.view.core.window.FloatWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.floatWindow',
	id:'floatWin',
	frame: true,
	closable: false,
	title: '',
	draggable:false,
	bodyStyle: {
		background: '#E0EEEE',
		visibility: 'visible',
		opacity: 0
	},
	width: 120,
	plain:true,
	resizable:false,
	renderTo: Ext.getBody(),
	dockedItems:[{
		xtype:'toolbar',
		dock:'top',
		id:'floatToolbar',
		style:{
			opacity: 0.6
		},
		width:130,
		items: [{
			xtype:'button',
			width: 120,
			style: {
				marginLeft: '34px'
			},
			cls: 'x-button-icon-query',
			text: '&nbsp;&nbsp;&nbsp;查看已选择',
			handler: function(btn){//查看暂存区
		    	var me = this, grid = Ext.getCmp('batchDealGridPanel'),form=Ext.getCmp('dealform');
		    	var  checkwin=Ext.getCmp('checkwin'+caller);
		        if(checkwin){
		        	checkwin.show();
		        }else{
  		       	  var checkwin =  Ext.create('Ext.Window', {
			    		id : 'checkwin'+caller,
					    height: "100%",
					    width: "80%",
					    maximizable : true,
						buttonAlign : 'center',
						layout : 'anchor',
						items : [btn.ownerCt.ownerCt.createGrid(grid)],
					    buttons : [{
					    	text :$I18N.common.button.erpExportButton,
					    	iconCls: 'x-button-icon-excel',
					    	cls: 'x-btn-gray',
					    	handler : function(btn){
					    		var checkgrid=Ext.getCmp('floatGrid');		
					    		grid.BaseUtil.exportGrid(checkgrid,checkgrid.title);
					    	}
					  } , {
					    	text : $I18N.common.button.erpCloseButton,
					    	iconCls: 'x-button-icon-close',
					    	cls: 'x-btn-gray',
					    	handler : function(btn){
					    		btn.ownerCt.ownerCt.close();
					    	}
					    }]
					});
					checkwin.show();
				}
		    }
		}]}],
	initComponent: function() {
		this.callParent(arguments);
		this.setPosition(Ext.getBody().getWidth()-210,Ext.getBody().getHeight()-65);
		this.show();
	},
	createGrid: function(grid){
		 var mm = this;
		 var co = [];
		 var data = grid.multiselected;
		 var fields = [];
		 Ext.each(grid.columns,function(col){
			 if(col.dataIndex != ""){
		       	  var o = new Object();
		       	  o.additionalCls = col.additionalCls;
		       	  o.align = col.align;
		       	  o.autoEdit = col.autoEdit;
		       	  o.border = col.border;
		       	  o.childCls = col.childCls;
		       	  o.componentCls = col.componentCls;
		       	  o.componentLayout = col.componentLayout;
		       	  o.componentLayoutCounter = col.componentLayoutCounter;
		       	  o.container = col.container;
		       	  o.dataIndex = col.dataIndex;
		       	  o.defaults = col.defaults;
		       	  o.el = col.el;
		       	  o.event = col.event;
		       	  o.filter = col.filter;
		       	  o.flex = col.flex;
		       	  o.format = col.format;
		       	  o.frameSize = col.frameSize;
		       	  o.fullName = col.fullName;
		       	  o.haveRendered = col.haveRendered;
		       	  o.headerCounter = col.headerCounter;
		       	  o.headerId = col.headerId;
		       	  o.height = col.height;
		       	  o.hidden = col.hidden;
		       	  o.hiddenAncestor = col.hiddenAncestor;
		       	  o.id = col.id+'2';
		       	  o.initialConfig = col.initialConfig;
		       	  o.items = col.items;
		       	  o.keyNav = col.keyNav;
		       	  o.layout = col.layout;
		       	  o.layoutManagedHeight = col.layoutManagedHeight;
		       	  o.layoutManagedWidth = col.layoutManagedWidth;
		       	  o.layoutOnShow = col.layoutOnShow;
		       	  o.managedListener = col.managedListener;
		       	  o.margins = col.margins;
		       	  o.minWidth = col.minWidth;
		       	  o.modify = col.modify;
		       	  o.needsLayout = col.needsLayout;
		       	  o.text = col.text;
		       	  o.width = col.width;
		       	  o.x = col.x
		       	  o.y = col.y;
		       	  o.xtype = col.xtype;
		       	  co.push(o);
		       	  fields.push(mm.getFields(col));
			 }
         });
         var grids = new Ext.grid.GridPanel({
	        	emptyText : $I18N.common.grid.emptyText,
	        	id : 'floatGrid',
	        	columnLines : true,
	        	autoScroll : true,
	        	height : '100%',
	        	bodyStyle: 'background-color:#f1f1f1;',
	        	requires: ['erp.view.core.grid.HeaderFilter', 'erp.view.core.plugin.CopyPasteMenu'],
		       	columns:{
		       		items:co
		       	},
		        plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
		            clicksToEdit: 1
		        }), Ext.create('erp.view.core.grid.HeaderFilter'), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
		        selModel: Ext.create('Ext.selection.CheckboxModel',{
		        	checkOnly : true,
		    		ignoreRightMouseSelection : false,
		    		listeners:{
		    	        selectionchange:function(selModel, selected, options){
		    	        	var thisGrid = selModel.view.ownerCt;
		    	        	if(selected.length == thisGrid.store.data.items.length){
		    	        		grid.multiselected = selected;
		    	        		grid.getSelectionModel().select(grid.multiselected,true);
		    	        	}
		    	        },
		    	        select:function(row,record,opt){
		    	        	grid.multiselected.push(record);
		    	        	grid.getSelectionModel().select(record,true);
		    	        },
		    	        deselect:function(row,record,opt){
		    	        	Ext.Array.remove(grid.multiselected, record);
		    	        	grid.getSelectionModel().deselect(record);
		    	        }
		    	    },
		    	    getEditor: function(){
		    	    	return null;
		    	    },
		    	    onHeaderClick: function(headerCt, header, e) {
		    	        if (header.isCheckerHd) {
		    	            e.stopEvent();
		    	            var isChecked = header.el.hasCls(Ext.baseCSSPrefix + 'grid-hd-checker-on');
		    	            if (isChecked && this.getSelection().length > 0) {//先全选,再筛选后再全选时,无法响应的bug
		    	                this.deselectAll(true);
		    	                grid.multiselected = [];
		    	                grid.getSelectionModel().deselectAll();
		    	            } else {
		    	                this.selectAll(true);
		    	                //grid.multiselected = headerCt.ownerCt.store.data.items;
		    	                
		    	                this.view.ownerCt.selectall = true;
		    	            }
		    	        }
		    	    }
		    	}),
		    	listeners:{
			    	afterrender: function(g){
		    			g.getSelectionModel().selectAll(true);
		    			g.readOnly = true;
			    	}
		    	}
         });
		 var store = grid.GridUtil.setStore(grids, fields, data, grid.groupField, grid.necessaryField);
		 grids.reconfigure(store);
         return grids;
	},
	getFields : function(col){
		var f = new Object();
		var type = col.xtype;
		if (type=="numbercolumn") {
			f.type = "number";
			f.format = "";
		} else if (type=="floatcolumn") {
			f.type = "number";
			f.format = "0.00";
		} else if (type.match("floatcolumn\\d{1}")) {
			f.type = "number";
			f.format = "0.";
			var length = parseInt(type.replace("floatcolumn", ""));
			for (var i = 0; i < length; i++) {
				f.format += "0";
			}
		} else if (type=="datecolumn") {
			f.type = "date";
			f.format = "Y-m-d";
		} else if (type=="datetimecolumn") {
			f.type = "date";
			f.format = "Y-m-d H:i:s";
		} else if (type.indexOf("booleancolumn")!=-1 || type.indexOf("boolean")!=-1) {
			f.type = "boolean";
		} else if (type.indexOf("checkcolumn")!=-1) {
			f.type = "bool";
		} else if (type.indexOf("tfcolumn")!=-1) {
			f.type = "tf";
		} else if (type.indexOf("yncolumn")!=-1) {
			f.type = "yn";
		} else {
			f.type = "string";
			f.format = "";
		}
		f.name = col.dataIndex;
		return f;
	}
});