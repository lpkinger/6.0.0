/**
 * 自定义grid.column
 * 
 * <pre>
 * 链接
 * <br>
 * example: 
 * linkUrl: jsps/scm/purchase/purchase.jsp?pu_id={pu_id}&pd_puid={pu_id} 
 * linkTabTitle: '采购单({pu_code})'
 * </pre>
 */
Ext.define('erp.view.core.grid.LinkColumn', {
	extend : 'Ext.grid.column.Column',
	alias : [ 'widget.linkcolumn' ],
	linkCls: 'x-btn-link',
	tpl: new Ext.XTemplate(
	'<div class="{linkCls}-wrap">',
		'<tpl for="links">',
			'<a class="{parent.linkCls}" data-index="{#}">{text}</a>',
		'</tpl>',
	'</div>'),
	initComponent: function(){
        var me = this;
        me.tpl = (!Ext.isPrimitive(me.tpl) && me.tpl.compile) ? me.tpl : new Ext.XTemplate(me.tpl);
        var origrenderer = me.renderer || me.defaultRenderer;
        me.renderer = function() {
        	return origrenderer.call(me, arguments[0], arguments[1], arguments[2],arguments[3], arguments[4], arguments[5], arguments[6]);
        };
        me.callParent(arguments);
    },
    defaultRenderer: function(value, meta, record) {
    	record.links = record.links || {};
    	var me = this, links = record.links[me.dataIndex] || [];
    	if(links.length == 0) {
    		links.push({text: me.linkText || value, handler: me.handler, url: me.linkUrl, title: me.linkTabTitle});
    		record.links[me.dataIndex] = links;
    	}
        var data = Ext.apply({
        	linkCls: me.linkCls,
        	links: links
        }, {
        	text: me.linkText || value
        }, record.data, record.getAssociatedData());
        return this.tpl.apply(data);
    },
    defaultHandler: function(view, cell, rowIdx, cellIdx, config, e) {
    	var me = this, row = e.getTarget(view.getItemSelector(), view.getTargetEl()), record = view.getRecord(row);
		openUrl2(me.parseStringByRecord(config.url, record), me.parseStringByRecord(config.title, record));
    },
    parseStringByRecord: function(str, record) {
    	var index = 0, length = str.length, s, p;
		while(index < length) {
			if((s = str.indexOf('{', index)) != -1 && (p = str.indexOf('}', s + 1)) != -1) {
				str = str.substring(0, s) + record.get(str.substring(s + 1, p)) + str.substring(p + 1);
				index = p + 1;
			} else {
				break;
			}
		}
		return str;
    },
    processEvent : function(type, view, cell, recordIndex, cellIndex, e){
        var me = this, target = e.getTarget(), key = type == 'keydown' && e.getKey();
        if (type == 'click' || (key == e.ENTER || key == e.SPACE)) {
        	if (Ext.fly(target).hasCls(me.linkCls)) {
        		var row = e.getTarget(view.getItemSelector(), view.getTargetEl()), record = view.getRecord(row);
        		var index = target.getAttribute("data-index"), config = record.links[me.dataIndex][index - 1],
					handler = (config.handler || me.defaultHandler);
	            handler && (handler.call(me, view, cell, recordIndex, cellIndex, config, e, record, row));
            }
        }
        return me.callParent(arguments);
    }
});