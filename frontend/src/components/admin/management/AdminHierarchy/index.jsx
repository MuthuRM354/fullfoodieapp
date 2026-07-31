import React from 'react';

const LEVELS = [
  { role: 'SUPER_ADMIN', color: '#8b5cf6', perms: ['Read', 'Write', 'Delete', 'Admin'] },
  { role: 'ADMIN',       color: '#3b82f6', perms: ['Read', 'Write', 'Delete'] },
  { role: 'MODERATOR',   color: '#22c55e', perms: ['Read', 'Write'] },
];

export default function AdminHierarchy() {
  return (
    <div style={{ padding: 24 }}>
      <h2 style={{ marginBottom: 20 }}>Admin Hierarchy</h2>
      {LEVELS.map(({ role, color, perms }) => (
        <div key={role} style={{ background: '#fff', borderRadius: 10, padding: '16px 20px', marginBottom: 12, borderLeft: `4px solid ${color}`, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
          <div style={{ fontWeight: 700, color, marginBottom: 6 }}>{role}</div>
          <div style={{ display: 'flex', gap: 8 }}>
            {perms.map(p => (
              <span key={p} style={{ background: `${color}20`, color, padding: '2px 10px', borderRadius: 20, fontSize: 12, fontWeight: 600 }}>{p}</span>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
