/**
 * From-To textfield
 * @author yingp
 */
/*Ext.define('erp.view.core.form.FtField', {
    extend: 'Ext.Component',
    mixins: {
        labelable: 'Ext.form.Labelable',
        field: 'Ext.form.field.Field'
    },
    alias: 'widget.erpFtField',
    fieldSubTpl: [
        '<input id="{id}_from" type="{type}" onkeydown="keydown(' + "'{name}'" 
        	+ ')" onkeyup="keydown(' + "'{name}'" + ')" onchange="keydown(' + "'{name}'" + ')" ',
        '<tpl if="name">name="{name}_from" </tpl>',
        '<tpl if="size">size="{size}" </tpl>',
        '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
        'class="{fieldCls} {typeCls}" autocomplete="off" />',
        '->',
        '<input id="{id}_to" type="{type}" onkeydown="keydown(' + "'{name}'" 
    		+ ')" onkeyup="keydown(' + "'{name}'" + ')" onchange="keydown(' + "'{name}'" + ')" ',
        '<tpl if="name">name="{name}_to" </tpl>',
        '<tpl if="size">size="{size}" </tpl>',
        '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
        'class="{typeCls}" autocomplete="off" />',
        '<span id="ft" style="color:#8EAECE;">From()To()</span>',
        {
            compiled: true,
            disableFormats: true
        }
    ],
    inputType: 'text',
    invalidText : 'The value in this field is invalid',
    fieldCls : Ext.baseCSSPrefix + 'form-field',
    focusCls : Ext.baseCSSPrefix + 'form-focus',
    dirtyCls : Ext.baseCSSPrefix + 'form-dirty',
    checkChangeEvents: Ext.isIE && (!document.documentMode || document.documentMode < 9) ?
                        ['change', 'propertychange'] :
                        ['change', 'input', 'textInput', 'keyup', 'dragdrop'],
    checkChangeBuffer: 50,
    componentLayout: 'field',
    readOnly : false,
    readOnlyCls: Ext.baseCSSPrefix + 'form-readonly',
    validateOnBlur: true,
    hasFocus : false,
    baseCls: Ext.baseCSSPrefix + 'field',
    maskOnDisable: false,
    initComponent : function() {
        var me = this;
        me.callParent();
        me.subTplData = me.subTplData || {};
        me.addEvents(
            'focus',
            'blur',
            'specialkey'
        );
        me.initLabelable();
        me.initField();
        if (!me.name) {
            me.name = me.getInputId();
        }
    },
    getInputId: function() {
        return this.inputId || (this.inputId = Ext.id());
    },
    getSubTplData: function() {
        var me = this,
            type = me.inputType,
            inputId = me.getInputId();
        return Ext.applyIf(me.subTplData, {
            id: inputId,
            cmpId: me.id,
            name: me.name || inputId,
            type: type,
            size: me.size || 20,
            cls: me.cls,
            fieldCls: me.fieldCls,
            tabIdx: me.tabIndex,
            typeCls: Ext.baseCSSPrefix + 'form-' + (type === 'password' ? 'text' : type)
        });
    },
    afterRender: function() {
        this.callParent();
        
        if (this.inputEl) {
            this.inputEl.selectable();
        }
    },
    getSubTplMarkup: function() {
        return this.getTpl('fieldSubTpl').apply(this.getSubTplData());
    },
    initRenderTpl: function() {
        var me = this;
        if (!me.hasOwnProperty('renderTpl')) {
            me.renderTpl = me.getTpl('labelableRenderTpl');
        }
        return me.callParent();
    },
    initRenderData: function() {
        return Ext.applyIf(this.callParent(), this.getLabelableRenderData());
    },
    setFieldStyle: function(style) {
        var me = this,
            inputEl = me.inputEl;
        if (inputEl) {
            inputEl.applyStyles(style);
        }
        me.fieldStyle = style;
    },
    onRender : function() {
        var me = this,
            fieldStyle = me.fieldStyle;
        me.onLabelableRender();
        me.addChildEls({ name: 'inputEl', id: me.getInputId() });
        me.callParent(arguments);
        me.setRawValue(me.rawValue);
        if (me.readOnly) {
            me.setReadOnly(true);
        }
        if (me.disabled) {
            me.disable();
        }
        if (fieldStyle) {
            me.setFieldStyle(fieldStyle);
        }
        me.renderActiveError();
    },
    initAria: function() {
        var me = this;
        me.callParent();
        me.getActionEl().dom.setAttribute('aria-describedby', Ext.id(me.errorEl));
    },
    getFocusEl: function() {
        return this.inputEl;
    },
    isFileUpload: function() {
        return this.inputType === 'file';
    },
    extractFileInput: function() {
        var me = this,
            fileInput = me.isFileUpload() ? me.inputEl.dom : null,
            clone;
        if (fileInput) {
            clone = fileInput.cloneNode(true);
            fileInput.parentNode.replaceChild(clone, fileInput);
            me.inputEl = Ext.get(clone);
        }
        return fileInput;
    },
    getSubmitData: function() {
        var me = this,
            data = null,
            val;
        if (!me.disabled && me.submitValue && !me.isFileUpload()) {
            val = me.getSubmitValue();
            if (val !== null) {
                data = {};
                data[me.getName()] = val;
            }
        }
        return data;
    },
    getSubmitValue: function() {
        return this.processRawValue(this.getRawValue());
    },
    getRawValue: function() {
        var me = this,
            v = (me.inputEl ? me.inputEl.getValue() : Ext.value(me.rawValue, ''));
        me.rawValue = v;
        return v;
    },
    setRawValue: function(value) {
        var me = this;
        value = Ext.value(value, '');
        me.rawValue = value;
        if (me.inputEl) {
            me.inputEl.dom.value = value;
        }
        return value;
    },
    valueToRaw: function(value) {
        return '' + Ext.value(value, '');
    },
    rawToValue: function(rawValue) {
        return rawValue;
    },
    processRawValue: function(value) {
        return value;
    },
    getValue: function() {
        var me = this,
            val = me.rawToValue(me.processRawValue(me.getRawValue()));
        var from = document.getElementsByName(me.id + '_from')[0].value;
        var to = document.getElementsByName(me.id + '_to')[0].value;
        if(from.length > me.maxLength){
        	document.getElementsByName(me.id + '_from')[0].value = from .substring(0, me.maxLength);showError('maxLength:' + me.maxLength);
        	from = from .substring(0, me.maxLength);showError('maxLength:' + me.maxLength);
        }
        if(to.length > me.maxLength){
        	to = to .substring(0, me.maxLength);showError('maxLength:' + me.maxLength);
        }
        from = from == null || from == '' ? to : from;
        to = to == null || to == '' ? from : to;
        val = val == null || val == '' ? 'BETWEEN ' + from + ' AND ' + to : val;
        me.value = val;
        document.getElementById('ft').innerHTML = 'From (' + from + ') To (' + to + ")";
        return val;
    },
    setValue: function(value) {
        var me = this;
        me.setRawValue(me.valueToRaw(value));
        return me.mixins.field.setValue.call(me, value);
    },
    onDisable: function() {
        var me = this,
            inputEl = me.inputEl;
        me.callParent();
        if (inputEl) {
            inputEl.dom.disabled = true;
        }
    },
    onEnable: function() {
        var me = this,
            inputEl = me.inputEl;
        me.callParent();
        if (inputEl) {
            inputEl.dom.disabled = false;
        }
    },
    setReadOnly: function(readOnly) {
        var me = this,
            inputEl = me.inputEl;
        if (inputEl) {
            inputEl.dom.readOnly = readOnly;
            inputEl.dom.setAttribute('aria-readonly', readOnly);
        }
        me[readOnly ? 'addCls' : 'removeCls'](me.readOnlyCls);
        me.readOnly = readOnly;
    },
    fireKey: function(e){
        if(e.isSpecialKey()){
            this.fireEvent('specialkey', this, Ext.create('Ext.EventObjectImpl', e));
        }
    },
    initEvents : function(){
        var me = this,
            inputEl = me.inputEl,
            onChangeTask,
            onChangeEvent;
        if (inputEl) {
            me.mon(inputEl, Ext.EventManager.getKeyEvent(), me.fireKey,  me);
            me.mon(inputEl, 'focus', me.onFocus, me);
            me.mon(inputEl, 'blur', me.onBlur, me, me.inEditor ? {buffer:10} : null);
            onChangeTask = Ext.create('Ext.util.DelayedTask', me.checkChange, me);
            me.onChangeEvent = onChangeEvent = function() {
                onChangeTask.delay(me.checkChangeBuffer);
            };
            Ext.each(me.checkChangeEvents, function(eventName) {
                if (eventName === 'propertychange') {
                    me.usesPropertychange = true;
                }
                me.mon(inputEl, eventName, onChangeEvent);
            }, me);
        }
        me.callParent();
    },
    doComponentLayout: function() {
        var me = this,
            inputEl = me.inputEl,
            usesPropertychange = me.usesPropertychange,
            ename = 'propertychange',
            onChangeEvent = me.onChangeEvent;
        if (usesPropertychange) {
            me.mun(inputEl, ename, onChangeEvent);
        }
        me.callParent(arguments);
        if (usesPropertychange) {
            me.mon(inputEl, ename, onChangeEvent);
        }
    },
    preFocus: Ext.emptyFn,
    onFocus: function() {
        var me = this,
            focusCls = me.focusCls,
            inputEl = me.inputEl;
        me.preFocus();
        if (focusCls && inputEl) {
            inputEl.addCls(focusCls);
        }
        if (!me.hasFocus) {
            me.hasFocus = true;
            me.componentLayout.onFocus();
            me.fireEvent('focus', me);
        }
    },
    beforeBlur : Ext.emptyFn,
    onBlur : function(){
        var me = this,
            focusCls = me.focusCls,
            inputEl = me.inputEl;

        if (me.destroying) {
            return;
        }
        me.beforeBlur();
        if (focusCls && inputEl) {
            inputEl.removeCls(focusCls);
        }
        if (me.validateOnBlur) {
            me.validate();
        }
        me.hasFocus = false;
        me.fireEvent('blur', me);
        me.postBlur();
    },
    postBlur : Ext.emptyFn,
    onDirtyChange: function(isDirty) {
        this[isDirty ? 'addCls' : 'removeCls'](this.dirtyCls);
    },
    isValid : function() {
        var me = this;
        return me.disabled || me.validateValue(me.processRawValue(me.getRawValue()));
    },
    validateValue: function(value) {
        var me = this,
            errors = me.getErrors(value),
            isValid = Ext.isEmpty(errors);
        if (!me.preventMark) {
            if (isValid) {
                me.clearInvalid();
            } else {
                me.markInvalid(errors);
            }
        }

        return isValid;
    },
    markInvalid : function(errors) {
        var me = this,
            oldMsg = me.getActiveError();
        me.setActiveErrors(Ext.Array.from(errors));
        if (oldMsg !== me.getActiveError()) {
            me.doComponentLayout();
        }
    },
    clearInvalid : function() {
        var me = this,
            hadError = me.hasActiveError();
        me.unsetActiveError();
        if (hadError) {
            me.doComponentLayout();
        }
    },
    renderActiveError: function() {
        var me = this,
            hasError = me.hasActiveError();
        if (me.inputEl) {
            me.inputEl[hasError ? 'addCls' : 'removeCls'](me.invalidCls + '-field');
        }
        me.mixins.labelable.renderActiveError.call(me);
    },
    getActionEl: function() {
        return this.inputEl || this.el;
    }
});
function keydown(name){
	Ext.getCmp(name).value = Ext.getCmp(name).getValue();
}*/
Ext.define('erp.view.core.form.FtField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.erpFtField',
    layout: 'column',
    value: "BETWEEN '' AND ''",
    height: 22,
    items: [],
    initComponent : function(){ 
    	this.callParent(arguments);
    	var me = this;
    	me.insert(0, {
	        xtype: 'textfield',
	        id: me.name + '_from',
	        name: me.name + '_from',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(f){
	        		var from = f.value;
	        		var to = me.items.items[1].value;
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        		} else {
	        			me.value = "BETWEEN '" + from + "' AND '" + to + "'";
	        		}
	        	}
	        }
	    });
    	me.insert(1, {
	        xtype: 'textfield',
	        id: me.name + '_to',
	        name: me.name + '_to',
	        columnWidth: 0.5,
	        fieldStyle: me.fieldStyle,
	        listeners: {
	        	change: function(f){
	        		var from = me.items.items[0].value;
	        		var to = f.value;
	        		from = from == null || from == '' ? to == null || to == '' ? '' : to : from;
	        		to = to == null || to == '' ? from == null || from == '' ? '' : from : to;
	        		if(from == ''){
	        			me.value = '';
	        		} else {
	        			me.value = "BETWEEN '" + from + "' AND '" + to + "'";
	        		}
	        	}
	        }
	    });
	},
	listeners: {
    	afterrender: function(){
    		var tb = this.getEl().dom;
    		if(tb.nodeName == 'TABLE') {
    			return;
    		}
    		tb.childNodes[1].style.height = 22;
    		tb.childNodes[1].style.overflow = 'hidden';
    	}
    },
    reset: function(){
		this.items.items[0].reset();
		this.items.items[1].reset();
	},
    getValue: function(){
    	if(this.value != null && this.value != ''){
    		if(this.items.items[0].value == null || this.items.items[0].value == ''){
    			return this.items.items[1].value + '~' + this.items.items[1].value;
    		} else if(this.items.items[1].value == null || this.items.items[1].value == ''){
    			return this.items.items[0].value + '~' + this.items.items[0].value;
    		} else {
    			return this.items.items[0].value + '~' + this.items.items[1].value;
    		}
    	} else {
    		return '';
    	}
    },
    isValid: function(){
    	return true;
    },
    setValue: function(value){
    	if(value != null && value != '' && contains(value, '~', true)){
    		this.items.items[0].setValue(value.split('~')[0]);
    		this.items.items[1].setValue(value.split('~')[1]);
    	}
    },
    getFilter: function() {
    	var me = this, fromVal = me.items.items[0].value, toVal = me.items.items[1].value;
    	return (fromVal || toVal) ? {
    		"gte": fromVal,
    		"lte": toVal
    	} : null;
    }
});