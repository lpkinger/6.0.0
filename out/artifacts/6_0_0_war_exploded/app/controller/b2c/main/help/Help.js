Ext.define('erp.controller.b2c.main.help.Help', {
    extend: 'Ext.app.Controller',
    views: ['b2c.main.help.Help'],
    init: function(){ 
    	var me = this;
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	me.MAXCARD = me.BaseUtil.getUrlParam("MAXCARD");
    	this.control({ 
    		'button[id=next]': { 
    			click: function(btn) {
					me.changePage(btn);
				}
    		},
    		'button[id=prev]': { 
    			click: function(btn) {
					me.changePage(btn);
				}
    		}
    	}),
    	this.changePage=function (btn) {
    		var test = me.BaseUtil.getUrlParam("TITLE");
			var index = Number(btn.ownerCt.ownerCt.layout.activeItem.id.substring(1));
			var el=btn.getEl();
			var next = Ext.getCmp('next');
			if(index==me.MAXCARD){
				if(btn.id == "next"){
					if(parent){
						var activePanel = parent.window.Ext.getCmp('content-panel').getActiveTab();
						activePanel.close();
					}
				}else {
					var next = Ext.getCmp('next');
					next.removeCls('end');
					next.addCls('next');
				}
			}
			if(btn.id == "prev") {
				index -= 1;
				if(index < 1) index = 1;
			} else {
				index += 1;
				if(index > me.MAXCARD) index = me.MAXCARD;
			}
			if(index==1){
				el.dom.disabled = true;
			}
			if(index==me.MAXCARD){
				next.removeCls('next');
				next.addCls('end');
				next.text='';
			}
			btn.ownerCt.ownerCt.layout.setActiveItem("c" + index);
		}
    }
});