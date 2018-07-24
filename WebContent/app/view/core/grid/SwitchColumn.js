// 开关列
Ext.define('erp.view.core.grid.SwitchColumn', {
	extend : 'Ext.grid.column.Column',
	alias : [ 'widget.switchcolumn' ],
	stopSelection: true,
	align: 'center',
	innerCls: Ext.baseCSSPrefix + 'grid-cell-inner-switch',
	switchCls: Ext.baseCSSPrefix + 'grid-cell-switch',
	checkedValue: 1,
    unCheckedValue: 0,
	constructor: function() {
        this.addEvents(
            'beforecheckchange',
            'checkchange'
        );
        this.callParent(arguments);
        Ext.util.CSS.createStyleSheet('.x-grid-cell-inner-switch{padding:3px 10px}.x-grid-cell-switch{display:inline-block;position:relative;font-size:14px;line-height:18px;height:18px;vertical-align:middle}.x-grid-cell-switch .x-grid-cell-switch-body{margin:0;display:inline-block;position:relative;width:36px;height:18px;border:1px solid #bfcbd9;outline:0;border-radius:12px;box-sizing:border-box;background:#bfcbd9;cursor:pointer;transition:border-color .3s,background-color .3s}.x-grid-cell-switch-checked .x-grid-cell-switch-body{border-color:#20a0ff;background-color:#20a0ff}.x-grid-cell-switch-btn{top:0;left:0;position:absolute;border-radius:100%;transition:transform .3s;width:14px;height:14px;background-color:#fff;transform:translate(2px,1px)}.x-grid-cell-switch-checked .x-grid-cell-switch-btn{transform:translate(18px,1px)}');
    },
	processEvent: function(type, view, cell, recordIndex, cellIndex, e, record, row) {
        var me = this, key = type === 'keydown' && e.getKey(), mousedown = type == 'mousedown';
        if (!me.disabled && (mousedown || (key == e.ENTER || key == e.SPACE))) {
        	if (!record) {// for ext4.0
        		row = e.getTarget(view.getItemSelector(), view.getTargetEl());
        		record = view.getRecord(row);
        	}
            var dataIndex = me.dataIndex, checked = !record.get(dataIndex);
            if (me.fireEvent('beforecheckchange', me, recordIndex, checked, record) !== false) {
                record.set(dataIndex, checked ? me.checkedValue : me.unCheckedValue);
                me.fireEvent('checkchange', me, recordIndex, checked, record);
                if (mousedown) {
                    e.stopEvent();
                }
                if (!me.stopSelection) {
                    view.selModel.selectByPosition({
                        row: recordIndex,
                        column: cellIndex
                    });
                }
                return false;
            } else {
                return !me.stopSelection;
            }
        } else {
            return me.callParent(arguments);
        }
    },
    defaultRenderer: function(value, meta, record) {
        return this.tpl.apply({
        	switchCls: this.switchCls,
        	checked: !!value
        });
    },
	tpl: new Ext.XTemplate(
		'<label class="{switchCls} <tpl if="checked">{switchCls}-checked</tpl>">' +
			'<span class="{switchCls}-body">' +
				'<span class="{switchCls}-btn"></span>' +
			'</span>' +
		'</label>')
});