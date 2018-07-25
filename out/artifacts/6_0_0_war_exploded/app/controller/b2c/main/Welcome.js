Ext.define('erp.controller.b2c.main.Welcome', {
    extend: 'Ext.app.Controller',
    views: ['b2c.main.Welcome'],
    init: function(){ 
    	var me = this;
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
			var index = Number(btn.ownerCt.ownerCt.layout.activeItem.id.substring(1));
			var el=btn.getEl();
			var next = Ext.getCmp('next');
			if(index==MAXCARD){
				if(btn.id == "next"){
					if(parent.window.Ext.getCmp('twin_2017000000')){
						parent.window.Ext.getCmp('twin_2017000000').close();
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
				if(index > MAXCARD) index = MAXCARD;
			}
			if(index==1){
				el.dom.disabled = true;
			}
			if(index==MAXCARD){
				
				next.removeCls('next');
				next.addCls('end');
				next.text='';
			}
			btn.ownerCt.ownerCt.layout.setActiveItem("c" + index);
		}
    }
});