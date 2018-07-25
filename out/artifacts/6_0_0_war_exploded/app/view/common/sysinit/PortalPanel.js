Ext.define('erp.view.common.sysinit.PortalPanel', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.portalpanel',
	cls: 'x-portal',
	bodyCls: 'x-portal-body',
	autoScroll: true,
	manageHeight: false,
	defaultType:'panel',
	initComponent : function() {
		var me = this;
		this.layout = {
				type : 'column'
		};
		this.callParent();
		this.addEvents({
			validatedrop: true,
			beforedragover: true,
			dragover: true,
			beforedrop: true,
			drop: true
		});
	},
	beforeLayout: function() {
		var items = this.layout.getLayoutItems(),
		len = items.length,
		firstAndLast = ['x-portal-column-first', 'x-portal-column-last'],
		i, item, last;
		for (i = 0; i < len; i++) {
			item = items[i];
			item.columnWidth = 1 / len;
			last = (i == len-1);
			if (!i) {
				if (last) {
					item.addCls(firstAndLast);
				} else {
					item.addCls('x-portal-column-first');
					item.removeCls('x-portal-column-last');
				}
			} else if (last) {
				item.addCls('x-portal-column-last');
				item.removeCls('x-portal-column-first');
			} else {
				item.removeCls(firstAndLast);
			}
		}

		return this.callParent(arguments);
	},
	initEvents : function(){
		this.callParent();
		//this.dd = Ext.create('Ext.app.PortalDropZone', this, this.dropConfig);
	},
	beforeDestroy : function() {
		if (this.dd) {
			this.dd.unreg();
		}
		this.callParent();
	}
});
