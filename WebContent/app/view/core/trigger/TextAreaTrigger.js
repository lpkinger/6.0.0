/**
 * textarea trigger
 */
Ext.define('erp.view.core.trigger.TextAreaTrigger', {
    extend: 'Ext.form.field.Trigger',
    alias: 'widget.textareatrigger',
    triggerCls: 'x-form-textarea-trigger',
    onTriggerClick: function() {
        var trigger = this,
        value = this.value;
        Ext.MessageBox.minPromptWidth = 600;
        Ext.MessageBox.defaultTextHeight = 200;
        Ext.MessageBox.style= 'background:#e0e0e0;';
        Ext.MessageBox.draggable = false;//不可拖动
        Ext.MessageBox.prompt("详细内容", '',
        function(btn, text) {
            if (trigger.editable && btn == 'ok') {
                if (trigger.ownerCt === undefined) {
                    trigger.record.set(trigger.name, text);
                } else {
                    trigger.setValue(text);
                }
            }
        },
        this, true, //表示文本框为多行文本框    
        value);
    },
    listeners: {
        focus: function(trigger) {
            if (trigger.ownerCt === undefined) {
                try {
                    var grid = Ext.getCmp(trigger.el.dom.parentNode.offsetParent.offsetParent.id);
                    if (grid !== undefined) {
                        trigger.owner = grid;
                        if(grid.readOnly){
                        	Ext.Array.forEach(grid.columns,function(c){
								if(c.dataIndex==trigger.owner.editingPlugin.activeEditor.field.name){
									if(!c.modify){
										trigger.setFieldStyle('background:#e0e0e0;');
										trigger.setEditable(false);
									}
								}
							});
                        }
                        if (trigger.owner.editingPlugin.activeEditor.field.id == trigger.id) {
                            trigger.record = trigger.owner.editingPlugin.activeRecord;
                        } else {
                            trigger.record = trigger.owner.selModel.lastSelected;
                        }
                    }
                } catch(e) {

                }
            }
        }
    },
    initComponent: function() {
    	if(!contains(this.id, '-', true)){
    		this.editable = !this.readOnly;
    	}else{
    		if(!this.editable){
    			this.fieldStyle= 'background:#e0e0e0;';
    		}
    	}
    	this.readOnly = false;//always show trigger
        this.callParent(arguments);
    },
    setReadOnly :function(bool){//流程审批时设置对应分组字段是可编辑状态
    	this.readOnly=false;
    	this.setEditable(!bool);
    }
}); 