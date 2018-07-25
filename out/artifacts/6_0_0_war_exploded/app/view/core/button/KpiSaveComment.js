/**
 * 收藏评语
 */
Ext.define('erp.view.core.button.KpiSaveComment',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpKPiSaveComment',
		param: [],
		id: 'erpKPiSaveComment',
		text: $I18N.common.button.erpKPiSaveComment,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});