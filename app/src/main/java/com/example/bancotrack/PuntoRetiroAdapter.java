package com.example.bancotrack;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView; // ⬅️ ¡Esta es la clave!
import java.util.List;

import java.util.List;

public class PuntoRetiroAdapter extends RecyclerView.Adapter<PuntoRetiroAdapter.PuntoViewHolder>{

    //Interfaz para notificar a la MainActivity cuando se hace clic en una tarjeta
    public interface OnPuntoListener {
        void onPuntoClick(PuntoRetiro puntoSeleccionado);
    }

    private final Context context;
    private List<PuntoRetiro> listaPuntos;
    private OnPuntoListener onPuntoListener;

    public PuntoRetiroAdapter(Context context, OnPuntoListener onPuntoListener) {
        this.context = context;
        this.onPuntoListener = onPuntoListener;
    }

    //Clase interna que mantiene las vistas de cada tarjeta
    public class PuntoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvNombre, tvDireccion, tvTipo;

        public PuntoViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePunto);
            tvDireccion = itemView.findViewById(R.id.tvDireccionPunto);
            tvTipo = itemView.findViewById(R.id.tvTipoPunto);
            itemView.setOnClickListener(this); // Asignar el click al elemento completo
        }

        @Override
        public void onClick(View v) {
            // Cuando se hace click, obtenemos el PuntoRetiro de esta posición
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onPuntoListener.onPuntoClick(listaPuntos.get(position));
            }
        }
    }

    //Métodos obligatorios del Adapter
    @NonNull
    @Override
    public PuntoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_punto_retiro, parent, false);
        return new PuntoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PuntoViewHolder holder, int position) {
        PuntoRetiro punto = listaPuntos.get(position);
        holder.tvNombre.setText(punto.getNombre());
        holder.tvDireccion.setText(punto.getDireccion());
        holder.tvTipo.setText(punto.getTipo());
    }

    @Override
    public int getItemCount() {
        return listaPuntos != null ? listaPuntos.size() : 0;
    }

    //Método para actualizar los datos
    public void setPuntos(List<PuntoRetiro> puntos) {
        this.listaPuntos = puntos;
        notifyDataSetChanged();
    }
}
