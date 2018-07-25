/**
 * 工序跳转
 */
Ext.define('erp.view.core.button.ProcessForward',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessForwardButton',
		param: [],
		id: 'erpProcessForwardButton',
		text: '工序跳转',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler : function(){
		}
	});