/**
 * 输入框下拉筛选插件
 */
Ext.define('erp.view.core.plugin.Search', {
	requires : ['erp.view.core.trigger.Dropdown'],
	ptype : 'search',
	constructor : function(cfg) {
		if (cfg) {
			Ext.apply(this, cfg);
		}
	},
	pageIndex: 0,
    pageSize: 10,
	init : function(field) {
		var me = this;
		me.searchfield = field;
		field.enableKeyEvents = true;
		field.on({
			keyup : function(m, l) {
				var j = me.getDropdown();
                if (l.keyCode === Ext.EventObject.ESC || !m.value) {
                    j.hide();
                    m.setValue("");
                    return
                } else {
                    j.show();
                }
                var h = j.getSelectionModel();
                var i = h.getLastSelected();
                var n = j.store.indexOf(i);
                var k = j.store.getCount() - 1;
                if (l.keyCode === Ext.EventObject.UP) {
                    if (n === undefined) {
                        h.select(0);
                    } else {
                        h.select(n === 0 ? k: (n - 1));
                    }
                } else {
                    if (l.keyCode === Ext.EventObject.DOWN) {
                        if (n === undefined) {
                            h.select(0);
                        } else {
                            h.select(n === k ? 0 : n + 1);
                        }
                    } else {
                        if (l.keyCode === Ext.EventObject.ENTER) {
                            l.preventDefault();
                            i && me.loadRecord(i);
                        } else {
                        	me.pageIndex = 0;
                            clearTimeout(me.searchTimeout);
                            me.searchTimeout = Ext.Function.defer(function() {
                            	me.search(m.value);
                            }, 50, me);
                        }
                    }
                }
			},
			focus: function(b) {
                if (b.value && me.getDropdown().store.getCount() > 0) {
                	me.getDropdown().show();
                }
            },
            blur: function() {
                var b = me.getDropdown();
                me.hideTimeout = Ext.Function.defer(b.hide, 500, b);
            }
		});
		me.getDropdown().on({
			itemclick: function(c, d, l, i, e) {
//				me.loadRecord(d);
            },
            changePage: function(c, d) {
            	me.pageIndex += d;
            	me.search(me.getField().getValue());
            },
            footerClick: function(c, d) {
                clearTimeout(me.hideTimeout);
                me.getField().focus();
            }
		});
	},
	getField: function() {
		return this.searchfield;
	},
	getGrid: function() {
		return this.searchgrid || (this.searchgrid = this.getField().column.ownerCt.ownerCt);
	},
	getDropdown: function() {
        return this.getField().dropdown 
        	|| (this.getField().dropdown = Ext.create('erp.view.core.trigger.Dropdown'));
    },
    loadRecord: function(d) {
        this.getDropdown().hide();
    },
    search: function(h) {
        var e = this.filter(h);
        if (this.pageIndex < 0) {
            this.pageIndex = 0;
        } else {
            if (this.pageIndex > Math.floor(e.length / this.pageSize)) {
                this.pageIndex = Math.floor(e.length / this.pageSize);
            }
        }
        var g = this.pageIndex * this.pageSize;
        var f = g + this.pageSize;
        this.getDropdown().setTotal(e.length);
        this.getDropdown().setStart(g);
        this.getDropdown().getStore().loadData(e.slice(g, f));
        this.getDropdown().alignTo(this.getField().getEl(), "bl");
        if (e.length === 0) {
            this.getDropdown().hide();
        } else {
            this.getDropdown().getSelectionModel().select(0);
        }
    },
    filter: function(p) {
        var z = {}, s = [], t = Ext.escapeRe(p), v = new RegExp(t, "i"),
        	f = this.getField().dataIndex, h;
        this.getGrid().getStore().each(function(a) {
        	h = a.get(f);
        	if (h && v.test(h) && !z[h]) {
        		z[h] = 1;
        		s.push({text : h});
        	}
        });
        return s;
    }
});