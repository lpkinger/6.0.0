/**
 * 明细行导入excel
 */	
Ext.define('erp.view.core.button.ExportDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExportDetailButton',
		iconCls: 'x-button-icon-excel',
		cls: 'x-btn-tb',
    	disabled: false,
    	menu: [{
			iconCls: 'main-msg',
	        text: '下载模板',
	        cls: ' ',
	        scope: this,
	        handler: function(btn){
	        	var grid = btn.parentMenu.floatParent.grid;
	        	var condition = grid.condition;
	        	grid.BaseUtil = Ext.create('erp.util.BaseUtil');
	    		if(!condition){
	    			var urlCondition = grid.BaseUtil.getUrlParam('gridCondition');
	    			urlCondition = urlCondition == null || urlCondition == "null" ? "" : urlCondition;
	    			urlCondition = urlCondition.replace(/IS/g, "=");
	    			condition = urlCondition;
	    		}
	    		condition = grid.gridCondition || condition;
	    		if(Ext.isEmpty(condition)) {
	    			grid.BaseUtil.exportGrid(grid,'','',1);//1,不导出合计
	    		} else if(typeof grid.onExport === 'function'){
	    			grid.onExport(grid.caller||caller, 'detailgrid', grid.gridCondition || condition);
	    		} else {
	    			grid.BaseUtil.createExcel(grid.caller||caller, 'detailgrid', grid.gridCondition || condition);
	    		}
			}
	    },{
	        text: '&nbsp;&nbsp;&nbsp;导入明细数据',
	        scope: this,
	        xtype: 'upexcel',
			iconCls: 'main-msg',
			//id: 'upexcel',
			cls: ' ',
			height: 23,
	        listeners: {
	        	afterrender: function(btn) {
	        		var b = btn.ownerCt.floatParent, grid = b.grid;
		        	if(grid.readOnly)
		        		btn.hide();
	        	},
	        	beforeimport: function(btn){
		        	var b = btn.ownerCt.floatParent, grid = b.grid;
		        	if(grid.readOnly) {// 可编辑的情况下，允许导入
						alert('当前单据不允许编辑，无法导入.');
						return false;
					}
		        	return true;
				} 
	        }
	    },{
	    	iconCls: 'main-msg',
	        text: '直接导入',
	        scope: this,
	        xtype: 'directimportupexcel',
			//id: 'directimport',
			hidden:true,
			cls: ' ',
			height: 23,
	        listeners: {
	        	afterrender: function(btn) {
	        		var b = btn.ownerCt.floatParent, grid = b.grid,form =Ext.getCmp('form');
	        		if(grid.directImport&&!grid.readOnly&&form&&form.keyField&&Ext.getCmp(form.keyField)&&Ext.getCmp(form.keyField).value){
		        		btn.show();
	        		}
	        	},
	        	beforeimport: function(btn){
		        	var b = btn.ownerCt.floatParent, grid = b.grid;
		        	if(grid.readOnly) {// 可编辑的情况下，允许导入
						alert('当前单据不允许编辑，无法导入.');
						return false;
					}
		        	return true;
				} 
	        }
	    }],
		initComponent : function(){ 
			this.callParent(arguments);
		},
		listeners: {
			afterrender: function(){
				this.grid = this.ownerCt.ownerCt;
			}
		}
	});