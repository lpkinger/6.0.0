Ext.define('erp.view.core.button.PurchaseNotifyCancelAll',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPurchaseNotifyCancelAllButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	text: '取消所有',
    	id: 'cancel',
    	style: {
    		marginLeft: '10px'
        },
        hidden: true,
        width: 80,
        initComponent : function(){ 
			this.callParent(arguments); 
		}
       });