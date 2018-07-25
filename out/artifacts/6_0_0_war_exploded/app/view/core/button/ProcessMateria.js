/**
 * 工序退料
 */
Ext.define('erp.view.core.button.ProcessMateria',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessMateriaButton',
		param: [],
		id: 'erpProcessMateriaButton',
		text: '工序退料',
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