Ext.define('erp.view.core.window.Toast', {
	toastId: 'toast-div',
	initComponent: function() {
		var me = this;
		me.createEl();
		me.callParant();
	},
	createEl: function() {
		return Ext.DomHelper.insertFirst(document.body, {
            'id': this.toastId,
            'class': 'toast-div'
        }, true);
	},
	msg: function(type, title, delay, context) {
		var me = this, b = Ext.get(me.toastId);
		if(!b)
			b = me.createEl();
        var d = Ext.String.format.apply(String, Array.prototype.slice.call(arguments, 3));
        var c = Ext.DomHelper.append(b, me.getMsgContext((type || 'info'), title, d), true);
        c.hide();
        c.slideIn("t").ghost("t", {
            delay: delay || 1000,
            remove: true
        });
	},
	getMsgContext: function(type, title, context) {
        return '<div class="toast x-message-box-' + type + '"><h3>' + title + "</h3><p>" + context + "</p></div>";
    },
	info: function(title, context) {
		this.msg('info', title, 1000, context);
	},
	warning: function(title, context) {
		this.msg('warning', title, 4000, context);
	},
	error: function(title, context) {
		this.msg('error', title, 5000, context);
	}
});