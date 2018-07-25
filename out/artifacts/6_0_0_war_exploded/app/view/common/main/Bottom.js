Ext.define('erp.view.common.main.Bottom',{ 
	extend: 'Ext.Toolbar', 
	alias: 'widget.erpBottom',
	collapsible : true, 
	initComponent : function(){ 
		Ext.apply(this, { 
			id:"bottom", 
			region:"south", 
			height:25, 
			items: [{
				iconCls: 'main-btn-user',
				cls: 'main-btn-left',
				text: "<font size='2' class='bottom_left'>" + $I18N.common.main.activeUser + "</font><a id='activeUser' style='color:blue;cursor:pointer;' class='bottom_left' >" + em_code + "(" + em_name + ")" + "</a>"
			},'-','账套:',{
				xtype: 'tbtext',
				name: 'sob',
				text: '<font color=blue>' + sobText + '</font>'
			},'->',{
				cls: 'process-lazy',
				xtype: 'tbtext',
				id: 'process-lazy',
				text: ''
			},
			{
				xtype: 'tbtext',
				html:'<span class="x-btn-icon main-btn-uuHelper"></span><span><a href="javascript:window.open(\''+basePath+'/ma/uuHelperList.action?page=1&pageSize=10'+'\');"style=text-decoration:none>UU助手</a><span>'
			},{
				xtype: 'tbtext',
				text: "<font color=#4D4D4D size=2>售后电话：400-830-1818</font>&nbsp;&nbsp;",
			},{
				xtype: 'tbtext',
				text: "<font color=#4D4D4D size=2>售后邮箱：info@usoftchina.com</font>&nbsp;&nbsp;",
			},{
				//iconCls: 'main-btn-link',
				//cls: 'main-btn-right',
				xtype:'tbtext',
				//text: "<font color=blue size=2>&nbsp;&nbsp;联系我们</font>&nbsp;&nbsp;",
				html:'<a href="javascript:window.open(\'http://www.usoftchina.com\');"style=text-decoration:none>联系我们</a>'

			}]
		}); 
		this.callParent(arguments); 
	},
	update: function(obj) {
		if (obj) {
			var s = this.down('tbtext[name=sob]');
			s.setText('<font color=blue>' + obj.sob + '</font>');
		}
	}
});