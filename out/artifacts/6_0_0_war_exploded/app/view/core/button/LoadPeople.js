/**
 * 制造单工序界面，载入加工人按钮
 */
Ext.define('erp.view.core.button.LoadPeople',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpLoadPeopleButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'loadpeoplebutton',
    	text: $I18N.common.button.erpLoadPeopleButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 98,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});