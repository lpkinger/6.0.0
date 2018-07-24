/**
 * 解除物料对应平台料号匹配关系
 */	
Ext.define('erp.view.core.button.RemoveUUId',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRemoveUUIdButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id:'removeuuid',
    	text: $I18N.common.button.erpRemoveUUIdButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}		
	});