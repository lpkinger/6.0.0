Ext.define('erp.view.ma.sql.Form', {
	extend: 'Ext.form.Panel',
	xtype: 'result-form',
	id:'sql_edit',
	controller: 'tab-view',
	layout: {
		type: 'vbox',
		align: 'stretch'
	},
	split: true,
	title:'工作区',
	items:[{
		html:'<textarea id="code" style="width: 100%; height: 100% "></textarea>',
		flex:1,
		listeners:{
			afterrender:function(f){
				f.ownerCt.editor=CodeMirror.fromTextArea(document.getElementById("code"), {
					lineNumbers: true,
					lineWrapping:true,
					mode:"text/x-sql",
					cursorHeight:1	               
				});
			}	
		}
	}],
	dockedItems: [{
		xtype: 'toolbar',
		dock: 'top',
		items: [{
			text:'执行',
			xtype: 'button',
			iconCls:'execute-icon',
			handler:'onExecute'	
		}, '-', {
			iconCls: null,
			text:'导出',
			xtype: 'button',
			iconCls:'export-icon',
			handler:'onExport'
		}]
	}],
	defaults: {
		anchor: '100%',
		labelWidth: 120
	}
});