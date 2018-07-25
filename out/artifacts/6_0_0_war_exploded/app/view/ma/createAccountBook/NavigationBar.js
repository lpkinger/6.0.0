Ext.define('erp.view.ma.createAccountBook.NavigationBar', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.navigationbar',
	layout : 'fit',
	border: 'none',
	padding: '2px 2px',
	style: 'text-align: center;',
	items : [{
		tplWriteMode : 'overwrite',
		data : [],
		tpl : [
			'<nav class="nav-step">',
		            '<a class="done">填写企业信息</lia>',
		            '<a>确认开账信息</a>',
		            '<a>激活账套</a>',
		    '</nav>'
		]
	}]
});
