/**
 *采购询价单：查看历史入库价
 */	
Ext.define('erp.view.core.button.DownLoadFile',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDownLoadFileButton',
		iconCls: 'x-button-icon-download',
    	cls: 'x-btn-gray',
    	id: 'downloadfile',
    	text: $I18N.common.button.erpDownloadButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 80,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});