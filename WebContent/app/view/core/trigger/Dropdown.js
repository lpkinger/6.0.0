/**
 * 输入框下拉筛选
 */
Ext.define("erp.view.core.trigger.Dropdown", {
    extend: "Ext.view.View",
    alias: "widget.searchdropdown",
    floating: true,
    autoShow: false,
    autoRender: true,
    toFrontOnShow: true,
    focusOnToFront: false,
    store: Ext.create('Ext.data.Store', {
    	fields: ["text"],
	    proxy: {
	        type: "memory",
	        reader: {
	            type: "json"
	        }
	    }
    }),
    trackOver: true,
    baseCls: Ext.baseCSSPrefix + 'boundlist',
    itemCls: Ext.baseCSSPrefix + 'boundlist-item',
    singleSelect: true,
    autoScroll: true,
    pageStart: 0,
    pageSize: 10,
    initComponent: function() {
    	var me = this, baseCls = me.baseCls, itemCls = me.itemCls;
	    me.selectedItemCls = baseCls + '-selected';
	    me.overItemCls = baseCls + '-item-over';
	    me.itemSelector = "." + itemCls;
	    if (me.floating) {
	        me.addCls(baseCls + '-floating');
	    }
        this.addEvents("changePage", "footerClick");
        this.tpl = new Ext.XTemplate('<tpl for=".">', 
        		'<div class="x-boundlist-item" style="min-width:40px;margin:0px 0px 5px 2px;">',
        		'<input type="checkbox">&nbsp;&nbsp;{text}</input>',
        		'</div>', 
        		"</div>", 
        		"</tpl>", 
        		'<div style="background:#e0e0e0;font-weight:800;height:25px;font-size:13px;" class="x-footer">', 
        		'<a href="#" class="x-prev" style="text-decoration:none">&nbsp;&lt;&nbsp;</a>', 
        		'<span>&nbsp;从&nbsp;{[this.getStart()+1]}&nbsp;到&nbsp;{[this.getEnd()]} 共 {[this.getTotal()]}&nbsp;&nbsp;</span>', 
        		'<a href="#" class="x-next" style="text-decoration:none">&nbsp;&gt;&nbsp;</a>', 
        		"</div>", 
        		'<div align="center"><button class="x-confirm">确定</button><button class="x-cancel">取消</button>', 
        		'</div>', {
            getTotal: Ext.bind(this.getTotal, this),
            getStart: Ext.bind(this.getStart, this),
            getEnd: Ext.bind(this.getEnd, this)
        });
        this.on("afterrender", function() {
            this.el.addListener("click", function() {
                this.fireEvent("changePage", this, -1);
            }, this, {
                preventDefault: true,
                delegate: ".x-prev"
            });
            this.el.addListener("click", function() {
                this.fireEvent("changePage", this, +1);
            }, this, {
                preventDefault: true,
                delegate: ".x-next"
            });
            this.el.addListener("click", function() {
                this.fireEvent("footerClick", this);
            },this, {
                delegate: ".x-footer"
            });
            this.el.addListener("click", function() {
                this.fireEvent("confirm", this);
            },this, {
                delegate: ".x-confirm"
            });
            this.el.addListener("click", function() {
                this.hide();
            },this, {
                delegate: ".x-cancel"
            });
        }, this);
        this.callParent(arguments);
    },
    setTotal: function(b) {
        this.total = b;
    },
    getTotal: function() {
        return this.total;
    },
    setStart: function(b) {
        this.pageStart = b;
    },
    getStart: function(b) {
        return this.pageStart;
    },
    getEnd: function(c) {
        var d = this.pageStart + this.pageSize;
        return d > this.total ? this.total: d;
    }
});