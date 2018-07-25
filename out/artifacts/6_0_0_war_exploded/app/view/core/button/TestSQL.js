/**
 * SQL生成器通用按钮设置
 */
Ext.define('erp.view.core.button.TestSQL', {
			extend : 'Ext.Button',
			alias : 'widget.erpSQLButton',
			cls : 'x-btn-gray-1',
			style : {
				marginLeft : '1px'
			},
			width : 60,
			initComponent : function() {
				this.callParent(arguments);
			}
		});