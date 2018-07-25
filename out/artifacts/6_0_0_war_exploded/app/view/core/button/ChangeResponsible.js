/**
 * 新增按钮
 */	
Ext.define('erp.view.core.button.ChangeResponsible',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChangeResponsibleButton',
		//iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'chanrespbtn',
    	oldValue:'',
    	ppd_id:'',
    	text: '变更责任人',
    	style: {
    		marginLeft: '10px'
        },
        width: 68,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});