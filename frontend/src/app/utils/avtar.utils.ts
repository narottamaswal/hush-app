const DARK_COLORS  = ['#1a1a2e','#16213e','#0f3460','#1b262c','#2d132c','#1c3144','#2b2d42','#3d2b1f','#1f3a3d','#2c2c54'];
const LIGHT_COLORS = ['#e8f4fd','#fef9e7','#eafaf1','#fdf2f8','#f0f4f8','#fffde7','#f3e5f5','#e8f5e9','#fff8e1','#fce4ec'];

export function generateAvatar(name: string, size = 80): string {
    const parts    = name.trim().split(/\s+/);
    const initials = parts.length >= 2
        ? (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
        : name.slice(0, 2).toUpperCase();

    const bg   = DARK_COLORS[Math.floor(Math.random()  * DARK_COLORS.length)];
    const text = LIGHT_COLORS[Math.floor(Math.random() * LIGHT_COLORS.length)];

    const canvas  = document.createElement('canvas');
    canvas.width  = size;
    canvas.height = size;
    const ctx     = canvas.getContext('2d')!;

    ctx.fillStyle = bg;
    ctx.fillRect(0, 0, size, size);

    ctx.fillStyle  = text;
    ctx.font       = `bold ${Math.floor(size * 0.38)}px system-ui, sans-serif`;
    ctx.textAlign  = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(initials, size / 2, size / 2);

    return canvas.toDataURL('image/png');
}