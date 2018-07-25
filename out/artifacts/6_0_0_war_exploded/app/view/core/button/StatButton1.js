// 带统计信息的按钮
Ext.define('erp.view.core.button.StatButton1', {
	extend : 'Ext.button.Button',
	alias : 'widget.erpStatButton1',
	onRender: function() {
        var me = this;
        me.addChildEls('statEl');    
        me.callParent(arguments);
    },
    stat: 0,
    show: true,
    setStat: function(stat){
    	var me = this, statEl = me.statEl;
    	me.stat = stat;
    	if(stat<=0||!me.show){
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
    			statEl.dom.innerHTML = '<div data-qtip="'+stat+'">···</div>';
    		}else{
    			statEl.dom.innerHTML = '<div data-qtip="'+stat+'">'+ stat+'</div>';
    		}
    	}
    },
    listeners:{
		afterrender:function(btn){
			btn.setStat(btn.stat);
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