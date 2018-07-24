Ext.define('erp.view.opensys.default.Footer', { 
	extend: 'Ext.Toolbar',  
	alias: 'widget.footer',
	cls:'cus-bottombg',
	border: false,
	bodyBorder:false,
	initComponent: function() { 
		Ext.apply(this, { 
			region:"south", 
			height:30, 
			items: [{
				/*cls:'cus-bottom-btn',*/
				text: "<font size='2' class='bottom_left'>" + $I18N.common.main.activeUser + "</font>"
			},{
				xtype: 'tbtext',			
				text: '<font color=blue>'+em_name+'('+em_code+')</font>'
			},'->',{
				cls:'cus-bottom-btn',
				text: "<font color=blue>http://www.usoftchina.com</font>&nbsp;&nbsp;",
				handler: function(){
					window.open('http://www.usoftchina.com');
				}
			}]
		}); 
		this.callParent(arguments); 
	}
});
