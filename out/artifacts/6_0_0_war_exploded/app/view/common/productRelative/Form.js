Ext.define('erp.view.common.productRelative.Form',{ 
	extend: 'Ext.form.Panel', 
	alias: 'widget.ProductRelativeFormPanel',
	id: 'queryform', 
    region: 'north',
    frame : true,
    header: false,//不显示title
	layout : 'column',
	autoScroll : true,
	defaultType : 'textfield',
	labelSeparator : ':',
	buttonAlign : 'center',
	fieldDefaults : {
	       margin : '2 2 2 2',
	       fieldStyle : "background:#FFFAFA;color:#515151;",
	       labelAlign : "right",
	       blankText : $I18N.common.form.blankText
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	
	initComponent : function(){ 
		var urlcondition = getUrlParam('condition');//从url解析参数
		urlcondition = (urlcondition == null) ? "" : urlcondition.replace(/IS/g,"=");
		var param = {caller: caller, condition: urlcondition};
    	this.FormUtil.getItemsAndButtons(this, 'common/singleFormItems.action', param);
		this.callParent(arguments);
		this.addKeyBoardEvents();
		
	},
	addKeyBoardEvents: function(){
		var me = this;
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					me.FormUtil.onAdd('form' + caller, 'Form配置维护(' + caller + ')', "jsps/ma/multiform.jsp?formCondition=fo_idIS" + me.fo_id + 
							"&gridCondition=fd_foidIS" + me.fo_id + "&whoami=" + caller);
				}
	    	});
		}
	}		
});