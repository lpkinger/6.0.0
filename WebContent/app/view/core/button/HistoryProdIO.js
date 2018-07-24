/**
 *制造单、委外单：出入库明细
 */	
Ext.define('erp.view.core.button.HistoryProdIO',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpHistoryProdIOButton',
		iconCls: 'x-button-icon-yuan',
    	cls: 'x-btn-gray',
    	id: 'historyprodio',
    	text: $I18N.common.button.erpHistoryProdIOButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 140,
		initComponent : function(){ 
			this.callParent(arguments); 
			this.addEvents({
				base: true,
				formal: true
			});
		}
	});