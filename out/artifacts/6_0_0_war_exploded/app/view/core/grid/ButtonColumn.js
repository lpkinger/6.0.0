/**
 * 自定义grid.column
 * 
 * <pre>
 * 显示按钮
 * </pre>
 */
Ext.define('erp.view.core.grid.ButtonColumn', {
	extend : 'Ext.grid.column.Column',
	alias : [ 'widget.buttoncolumn' ],
	buttonCls: 'x-grid-cell-button',
	tpl: new Ext.XTemplate(
	'<div class="{buttonCls}-wrap">',
		'<tpl for="buttons">',
			'<button class="{parent.buttonCls}" data-index="{#}">{text}</button>',
		'</tpl>',
	'</div>'),
	initComponent: function(){
        var me = this;
        me.buttons = me.buttons || [];
    	if(me.buttons.length == 0 && me.buttonText) {
    		me.buttons.push({text: me.buttonText, handler: me.handler});
    		delete me.buttonText;
    		delete me.handler;
    	}
        me.tpl = (!Ext.isPrimitive(me.tpl) && me.tpl.compile) ? me.tpl : new Ext.XTemplate(me.tpl);
        var origrenderer = me.renderer || me.defaultRenderer;
        me.renderer = function() {
        	return origrenderer.call(me, arguments[0], arguments[1], arguments[2]);
        };
        me.callParent(arguments);
    },
    defaultRenderer: function(value, meta, record) {
        var data = Ext.apply({
        	buttonCls: this.buttonCls,
        	buttons: this.buttons
        }, record.data, record.getAssociatedData());
        return this.tpl.apply(data);
    },
    processEvent : function(type, view, cell, recordIndex, cellIndex, e, record, row){
        var me = this, target = e.getTarget(), key = type == 'keydown' && e.getKey();
        if (type == 'click' || (key == e.ENTER || key == e.SPACE)) {
        	if (Ext.fly(target).hasCls(me.buttonCls)) {
        		var index = target.getAttribute("data-index"), handler = me.buttons[index - 1].handler;
                handler && (handler.call(me, view, cell, recordIndex, cellIndex, e));
            }
        }
        return me.callParent(arguments);
    }
});