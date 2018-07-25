Ext.define('erp.view.core.picker.Tip', {
	extend : 'Ext.Component',
	floating : true,
	text : null,
	direction : 'bottom',
	clsFile : basePath + 'resource/css/tip.css',
	injectStyleSheet: function(cssFile) {
		Ext.util.CSS.swapStyleSheet('tipcss', cssFile);
    },
    initComponent : function() {
    	var me = this;
    	me.injectStyleSheet(me.clsFile);
    	Ext.apply(me.renderData, {
    		cls : 'custom-tip',
    		text : me.text,
    		direction : me.direction
    	});
        me.callParent();
        me.showAt(me.getPosition());
    },
    getPosition : function() {
    	var me = this;
    	if (me.target) {
        	var p = me.target.getXY(), x = p[0], y = p[1], d = me.direction;
        	switch (d) {
	        	case 'top':
	    			y += 20;break;
	    		case 'bottom':
	    			y -= 20;break;
	    		case 'left':
	    			x += 20;break;
	    		case 'right':
	    			x -= 20;break;	
        	}
        	return [x, y];
        }
    	return null;
    },
    renderTpl: [
                	'<div class="{cls}">',
	                	'<div class="{cls}-body">',
	                		'<div class="{cls}-body-text">{text}</div>',
	                	'</div>',
	                	'<div class="{cls}-arrow-{direction}"></div>',
	                	'<div class="{cls}-arrow-{direction}-i"></div>',
                	'</div>'
            ]
});