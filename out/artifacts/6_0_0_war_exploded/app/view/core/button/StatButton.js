// 带统计信息的按钮
Ext.define('erp.view.core.button.StatButton', {
	extend : 'Ext.button.Button',
	alias : 'widget.erpStatButton',
	count:0,
	onRender: function() {
        var me = this;
        me.addChildEls('statEl');    
        me.callParent(arguments);
    },
    getTemplateArgs: function(){
    	var me = this;
    	return Ext.apply(me.callParent(arguments), {
    		stat : me.stat || 0
    	});
    },
    setStat: function(stat){
    	var me = this, statEl = me.statEl, oldStat = me.stat;
    	me.count = stat;
    	if(stat<=0){
    		if(!statEl.dom.classList.contains('statbtnhide')){
    			statEl.dom.classList.add('statbtnhide');
    		}
    		return;
    	}else{
    		if(statEl.dom.classList.contains('statbtnhide')){
    			statEl.dom.classList.remove('statbtnhide');
    		}
    	}
    	if (statEl) {
    		if(stat>99){
    			stat = '···';    			
    		}
    		
    		statEl.dom.innerHTML = stat;
    		me.stat = stat;
    		me.fireEvent('statchange', me, oldStat, stat);
    	}
    },
    getCount: function(){
    	return this.count;
    },   
    listeners:{
		afterrender:function(btn){
			if(!btn.statEl.dom.classList.contains('statbtnhide')){
				btn.statEl.dom.classList.add('statbtnhide');
			}
		}
    },
    renderTpl:
        '<div id="{id}-btnWrap" class="{splitCls}">' +
            '<tpl if="!href">' +
                '<div id="{id}-btnEl" type="{type}" hidefocus="true"' +
                    '<tpl if="tabIndex"> tabIndex="{tabIndex}"</tpl> role="button" autocomplete="off">' +
                    '<span id="{id}-btnInnerEl" class="{baseCls}-inner" style="{innerSpanStyle}">' +
                        '{text}' + 
                        '<span id="{id}-statEl" class="{baseCls}-stat">{stat}</span>' +
                    '</span>' +
                    '<span id="{id}-btnIconEl" class="{baseCls}-icon {iconCls}">&#160;</span>' +
                '</div>' +
            '</tpl>' +
        '</div>'
});