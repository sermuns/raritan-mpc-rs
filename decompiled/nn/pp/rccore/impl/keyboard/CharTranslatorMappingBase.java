/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.rccore.impl.keyboard;

import nn.pp.core.kbd.EricVKConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorConstants;
import nn.pp.rccore.impl.keyboard.CharTranslatorMapping;

public class CharTranslatorMappingBase
extends CharTranslatorMapping
implements CharTranslatorConstants,
EricVKConstants {
    @Override
    public void addMappings() {
        super.addMappings();
        this.addMapping('\t', new int[]{9, 65545});
        this.addMapping('\n', new int[]{10, 65546});
        this.addMapping(' ', new int[]{32, 65568});
        this.addMapping('1', new int[]{262144, 0x100000, 49, 65585});
        this.addMapping('2', new int[]{262144, 0x100000, 50, 65586});
        this.addMapping('3', new int[]{262144, 0x100000, 51, 65587});
        this.addMapping('4', new int[]{262144, 0x100000, 52, 65588});
        this.addMapping('5', new int[]{262144, 0x100000, 53, 65589});
        this.addMapping('6', new int[]{262144, 0x100000, 54, 65590});
        this.addMapping('7', new int[]{262144, 0x100000, 55, 65591});
        this.addMapping('8', new int[]{262144, 0x100000, 56, 65592});
        this.addMapping('9', new int[]{262144, 0x100000, 57, 65593});
        this.addMapping('0', new int[]{262144, 0x100000, 48, 65584});
        this.addMapping('!', new int[]{131072, 0x100000, 49, 65585});
        this.addMapping('@', new int[]{131072, 0x100000, 50, 65586});
        this.addMapping('#', new int[]{131072, 0x100000, 51, 65587});
        this.addMapping('$', new int[]{131072, 0x100000, 52, 65588});
        this.addMapping('%', new int[]{131072, 0x100000, 53, 65589});
        this.addMapping('^', new int[]{131072, 0x100000, 54, 65590});
        this.addMapping('&', new int[]{131072, 0x100000, 55, 65591});
        this.addMapping('*', new int[]{131072, 0x100000, 56, 65592});
        this.addMapping('(', new int[]{131072, 0x100000, 57, 65593});
        this.addMapping(')', new int[]{131072, 0x100000, 48, 65584});
        this.addMapping('q', new int[]{81, 65617});
        this.addMapping('Q', new int[]{81, 65617});
        this.addMapping('w', new int[]{87, 65623});
        this.addMapping('W', new int[]{87, 65623});
        this.addMapping('e', new int[]{69, 65605});
        this.addMapping('E', new int[]{69, 65605});
        this.addMapping('r', new int[]{82, 65618});
        this.addMapping('R', new int[]{82, 65618});
        this.addMapping('t', new int[]{84, 65620});
        this.addMapping('T', new int[]{84, 65620});
        this.addMapping('y', new int[]{89, 65625});
        this.addMapping('Y', new int[]{89, 65625});
        this.addMapping('u', new int[]{85, 65621});
        this.addMapping('U', new int[]{85, 65621});
        this.addMapping('i', new int[]{73, 65609});
        this.addMapping('I', new int[]{73, 65609});
        this.addMapping('o', new int[]{79, 65615});
        this.addMapping('O', new int[]{79, 65615});
        this.addMapping('p', new int[]{80, 65616});
        this.addMapping('P', new int[]{80, 65616});
        this.addMapping('a', new int[]{65, 65601});
        this.addMapping('A', new int[]{65, 65601});
        this.addMapping('s', new int[]{83, 65619});
        this.addMapping('S', new int[]{83, 65619});
        this.addMapping('d', new int[]{68, 65604});
        this.addMapping('D', new int[]{68, 65604});
        this.addMapping('f', new int[]{70, 65606});
        this.addMapping('F', new int[]{70, 65606});
        this.addMapping('g', new int[]{71, 65607});
        this.addMapping('G', new int[]{71, 65607});
        this.addMapping('h', new int[]{72, 65608});
        this.addMapping('H', new int[]{72, 65608});
        this.addMapping('j', new int[]{74, 65610});
        this.addMapping('J', new int[]{74, 65610});
        this.addMapping('k', new int[]{75, 65611});
        this.addMapping('K', new int[]{75, 65611});
        this.addMapping('l', new int[]{76, 65612});
        this.addMapping('L', new int[]{76, 65612});
        this.addMapping('z', new int[]{90, 65626});
        this.addMapping('Z', new int[]{90, 65626});
        this.addMapping('x', new int[]{88, 65624});
        this.addMapping('X', new int[]{88, 65624});
        this.addMapping('c', new int[]{67, 65603});
        this.addMapping('C', new int[]{67, 65603});
        this.addMapping('v', new int[]{86, 65622});
        this.addMapping('V', new int[]{86, 65622});
        this.addMapping('b', new int[]{66, 65602});
        this.addMapping('B', new int[]{66, 65602});
        this.addMapping('n', new int[]{78, 65614});
        this.addMapping('N', new int[]{78, 65614});
        this.addMapping('m', new int[]{77, 65613});
        this.addMapping('M', new int[]{77, 65613});
        this.addMapping('`', new int[]{262144, 0x100000, 192, 65728});
        this.addMapping('-', new int[]{262144, 0x100000, 45, 65581});
        this.addMapping('=', new int[]{262144, 0x100000, 61, 65597});
        this.addMapping('\\', new int[]{262144, 0x100000, 92, 65628});
        this.addMapping('[', new int[]{262144, 0x100000, 91, 65627});
        this.addMapping(']', new int[]{262144, 0x100000, 93, 65629});
        this.addMapping(';', new int[]{262144, 0x100000, 59, 65595});
        this.addMapping('\'', new int[]{262144, 0x100000, 222, 65758});
        this.addMapping(',', new int[]{262144, 0x100000, 44, 65580});
        this.addMapping('.', new int[]{262144, 0x100000, 46, 65582});
        this.addMapping('/', new int[]{262144, 0x100000, 47, 65583});
        this.addMapping('~', new int[]{131072, 0x100000, 192, 65728});
        this.addMapping('_', new int[]{131072, 0x100000, 45, 65581});
        this.addMapping('+', new int[]{131072, 0x100000, 61, 65597});
        this.addMapping('|', new int[]{131072, 0x100000, 92, 65628});
        this.addMapping('{', new int[]{131072, 0x100000, 91, 65627});
        this.addMapping('}', new int[]{131072, 0x100000, 93, 65629});
        this.addMapping(':', new int[]{131072, 0x100000, 59, 65595});
        this.addMapping('\"', new int[]{131072, 0x100000, 222, 65758});
        this.addMapping('<', new int[]{131072, 0x100000, 44, 65580});
        this.addMapping('>', new int[]{131072, 0x100000, 46, 65582});
        this.addMapping('?', new int[]{131072, 0x100000, 47, 65583});
    }
}

