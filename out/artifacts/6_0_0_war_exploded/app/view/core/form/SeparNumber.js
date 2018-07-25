/***
 * 支持千分位NumberField
 * 
 * @author yingp 
 */
Ext.define('erp.view.core.form.SeparNumber', {
    extend: 'Ext.form.field.Number',
    alias: 'widget.separnumberfield',
    initComponent: function(){
    	var me = this;
    	me.hideTrigger = true;
    	me.subTplData = me.subTplData || {};
    	Ext.apply(me.subTplData, {
    		suffix : '-separ',
    		enableSeparator : true
    	});
    	me.callParent(arguments);
    },
    fieldSubTpl: [ 
                   '<input id="{id}{suffix}" type="text" style="width: 100%;display: {enableSeparator:this.showTextField};" {inputAttrTpl}',
	                   ' size="1"',
	                   '<tpl if="name"> name="{name}{suffix}"</tpl>',
	                   '<tpl if="value"> value="{value:this.formatValue}"</tpl>',
	                   '<tpl if="placeholder"> placeholder="{placeholder}"</tpl>',
	                   '{%if (values.maxLength !== undefined){%} maxlength="{maxLength}"{%}%}',
	                   '<tpl if="readOnly"> readonly="readonly"</tpl>',
	                   '<tpl if="disabled"> disabled="disabled"</tpl>',
	                   '<tpl if="tabIdx"> tabIndex="{tabIdx}"</tpl>',
	                   '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>',
	                   ' class="{fieldCls} {typeCls} {editableCls} {inputCls}" autocomplete="off"/>',
	               '<input id="{id}" type="{type}" style="width: 100%;display: {enableSeparator:this.showNumberField};" ',
		               '<tpl if="name">name="{name}" </tpl>',
		               '<tpl if="value"> value="{[Ext.util.Format.htmlEncode(values.value)]}"</tpl>',
		               '<tpl if="size">size="{size}" </tpl>',
		               '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
		               'class="{fieldCls} {typeCls}" autocomplete="off" />',
		           '<div id="{cmpId}-triggerWrap" class="{triggerWrapCls}" role="presentation">',
		               '{triggerEl}',
		               '<div class="{clearCls}" role="presentation"></div>',
		           '</div>',
                   {
                       showNumberField : function(e) {
                    	   return e === true ? 'none' : 'block';
                       },
                       showTextField : function(e) {
                    	   return e === true ? 'block' : 'none';
                       },
                       formatValue : function(v) {
                    	   v = isNaN(v) ? '0' : v;
                    	   return Ext.util.Format.number(String(v), "0,000,000.00");
                       }
                   }
               ],
    listeners : {
    	afterrender : function() {
    		var me = this, n = me.getInputId(), s = n + me.subTplData.suffix;
    		me.numberEl = Ext.get(n);
    		me.textEl = Ext.get(s);
    		var c = Ext.Function.bind(me.changeEditStatus, me);
    		Ext.EventManager.on(me.numberEl, {
    			blur : c,
    			scope : me,
    			buffer : 100
    		});
    		Ext.EventManager.on(me.textEl, {
    			focus : c,
    			scope: me,
    			buffer : 100
    		});
    		setTimeout(function(){
        		if(me.textEl)
        			me.textEl.dom.value = me.formatValue(me.getValue());
        	}, 200);
    	},
    	change : function() {
    		if(this.textEl)
    			this.textEl.dom.value = this.formatValue(this.getValue());
    		this.setValue(this.numberFormat(this.getValue(), 2));
    	}
    },
    changeEditStatus : function(e) {
    	var me = this;
    	if (e && e.type == 'focus') {
    		me.textEl.dom.style.display = 'none';
    		me.numberEl.dom.style.display = 'block';
    		me.numberEl.dom.focus();
    		me.numberEl.dom.select();
    	} else {
    		me.numberEl.dom.style.display = 'none';
    		me.textEl.dom.style.display = 'block';
    		me.textEl.dom.value = me.formatValue(this.getValue());
    	}
    },
    formatValue : function(v) {
 	   v = isNaN(v) ? '0' : v;
 	   return Ext.util.Format.number(String(v), "0,000,000.00");
    },
    setFieldStyle : function(s) {
    	var me = this;
    	me.callParent(arguments);
    	setTimeout(function(){
    		if(me.textEl)
    			me.textEl.setStyle(s);
    	}, 200);
    },
    numberFormat:function(a, b) {
    	if(a < 0){
    		return Number('-'+Math.round(Math.abs(a)*Math.pow(10,b))/Math.pow(10,b));
    	} else {
    		return Math.round(a*Math.pow(10,b))/Math.pow(10,b);
    	}
	}
});