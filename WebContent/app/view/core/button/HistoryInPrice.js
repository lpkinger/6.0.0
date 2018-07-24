/**
 *采购询价单：查看历史入库价
 */	
Ext.define('erp.view.core.button.HistoryInPrice',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHistoryInButton',
		iconCls: 'x-button-icon-yuan',
    	cls: 'x-btn-gray',
    	id: 'historyin',
    	text: $I18N.common.button.erpHistoryInButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({
				base: true,
				formal: true
			});
		}
	});