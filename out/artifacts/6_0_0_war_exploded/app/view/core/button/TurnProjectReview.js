Ext.define('erp.view.core.button.TurnProjectReview',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProjectReviewButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray',
    	id: 'TurnProjectReview',
    	tooltip: '转项目评审',
    	id: 'erpTurnProjectReviewButton',
    	text: $I18N.common.button.erpTurnProjectReviewButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 120,
		handler: function(){
		}
	});