Ext.define('erp.view.core.button.ExportExcelButton',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExportExcelButton',
		iconCls: 'x-button-icon-excel',
		style: {
    		marginLeft: '10px'
        },
        width: 100, 
        exportCaller:null,
        id:'exportexcel',
        cls: 'x-btn-gray',
	    bodyStyle: 'background: transparent no-repeat 0 0; border:0;background-color:#f0f0f0',
    	disabled: false,
    	text: $I18N.common.button.erpExportExcelButton,
    	menu: [{
			iconCls: 'main-msg',
	        text: '下载模板',
	        cls: ' ',
	        scope: this,
	        handler: function(btn){
	        	var form=btn.parentMenu.floatParent.ownerCt.ownerCt;
	            var botton=Ext.getCmp('exportexcel'); 
	        	form.BaseUtil.createExcel(botton.exportCaller, 'detailgrid','1=2');
			}
	    },{
	        text: '导入EXCEL',
	        scope: this,
	        id:'importexcel',
	        alias: 'widget.erpImportExcelButton',
	        xtype:'form',
	        iconCls: 'main-msg',
			cls: ' ',
			height: 23,
	        bodyStyle: 'background: transparent no-repeat 0 0;border: none;',
			items: [{
				xtype: 'filefield',
				name: 'file',
				id:'exportfield',
				buttonOnly: true,
		        hideLabel: true,
		        width: 80,
		        height: 17,
				buttonConfig: {
			        iconCls: 'x-button-icon-excel',
			        text: '导入EXCEL数据'
		        },
		        id:'excelfile'
		      }]
			
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