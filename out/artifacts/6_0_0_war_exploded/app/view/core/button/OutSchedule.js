/**
 * 排程按钮
 */	
Ext.define('erp.view.core.button.OutSchedule',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpOutScheduleButton',
		//iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'outschedule',
	    disabled: true,
    	text: $I18N.common.button.erpOutScheduleButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
});