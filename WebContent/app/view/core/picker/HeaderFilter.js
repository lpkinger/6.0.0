/**
 * Grid HeaderContainer Filter Picker
 */
Ext.define('erp.view.core.picker.HeaderFilter', {
	extend : 'Ext.Component',
	requires : 'Ext.XTemplate',
	alias : 'widget.headerfilterpicker',
	componentCls : 'x-face-picker',
	selectedCls : 'x-face-picker-selected',
	itemCls : 'x-face-picker-item',
	style : 'background: #f1f2f5;',
	initComponent : function() {
		var me = this;
		me.callParent(arguments);
	},
	onRender : function(container, position) {
		var me = this;
		Ext.apply(me.renderData, {
			itemCls : me.itemCls
		});
		me.callParent(arguments);
	},
	afterRender : function(){
    	var me = this;
    	me.callParent(arguments);
    	this.el.addListener("click", function() {
            if (!this.fireEvent("confirm", this)) {
            	this.onConfirm();
            }
        },this, {
            delegate: ".x-confirm"
        });
        this.el.addListener("click", function() {
        	this.fireEvent("cancel", this);
        	this.hide();
        },this, {
            delegate: ".x-cancel"
        });
    },
	renderTpl : [
	            '<div style="margin:6px 5px 5px 5px;" align="center">',
	            	'<input name="查找" class="x-form-field"/>',
	            '</div>',
	            '<div style="margin:6px 5px 5px 5px;" align="center">',
	            	'<input type="checkbox">&nbsp;&nbsp;全选</input>',
	            '</div>',
	            '<div align="center"><button class="x-confirm">确定</button><button class="x-cancel">取消</button>', 
        		'</div>'
    ],
    refresh : function(grid, headerCt, header) {
    	
    },
    onConfirm : function() {
    	
    }
});